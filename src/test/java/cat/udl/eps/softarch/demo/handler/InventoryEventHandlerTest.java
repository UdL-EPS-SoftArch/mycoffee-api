package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Business;
import cat.udl.eps.softarch.demo.domain.Inventory;
import cat.udl.eps.softarch.demo.domain.InventoryStatus;
import cat.udl.eps.softarch.demo.domain.InventoryType;
import cat.udl.eps.softarch.demo.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventoryEventHandlerTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private InventoryEventHandler inventoryEventHandler;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void handleBeforeCreate_ShouldSetDefaults() {
        // Arrange
        Inventory inventory = new Inventory();
        Business business = new Business();
        business.setUsername("businessOwner");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(business);

        // Act
        inventoryEventHandler.handleBeforeCreate(inventory);

        // Assert
        assertEquals(InventoryStatus.ACTIVE, inventory.getStatus());
        assertEquals(InventoryType.WAREHOUSE, inventory.getType());
        assertNotNull(inventory.getLastUpdated());
        assertEquals(business, inventory.getBusiness());
    }

    @Test
    void handleBeforeSave_ShouldUpdateLastUpdated() {
        // Arrange
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        Business business = new Business();
        business.setUsername("owner");
        inventory.setBusiness(business);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("owner");

        // Act
        inventoryEventHandler.handleBeforeSave(inventory);

        // Assert
        assertNotNull(inventory.getLastUpdated());
    }

    @Test
    void handleBeforeSave_ShouldSetStatusFull_WhenCapacityReached() {
        // Arrange
        Inventory inventory = Mockito.mock(Inventory.class);
        Business business = new Business();
        business.setUsername("owner");

        when(inventory.getBusiness()).thenReturn(business);
        when(inventory.getCapacity()).thenReturn(100);
        when(inventory.getTotalStock()).thenReturn(150); // Over capacity
        when(inventory.getStatus()).thenReturn(InventoryStatus.ACTIVE);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("owner");

        // Act
        inventoryEventHandler.handleBeforeSave(inventory);

        // Assert
        Mockito.verify(inventory).setStatus(InventoryStatus.FULL);
    }
}
