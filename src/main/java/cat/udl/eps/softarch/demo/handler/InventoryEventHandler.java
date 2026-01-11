package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Business;
import cat.udl.eps.softarch.demo.domain.Inventory;
import cat.udl.eps.softarch.demo.domain.InventoryStatus;
import cat.udl.eps.softarch.demo.domain.InventoryType;
import cat.udl.eps.softarch.demo.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class InventoryEventHandler {
    private final Logger logger = LoggerFactory.getLogger(InventoryEventHandler.class);
    private final InventoryRepository inventoryRepository;

    public InventoryEventHandler(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @HandleBeforeCreate
    public void handleBeforeCreate(Inventory inventory) {
        logger.info("Before creating inventory: {}", inventory);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Must be logged in to create inventory");
        }

        if (auth.getPrincipal() instanceof Business business) {
            inventory.setBusiness(business);
        } else {
            throw new AccessDeniedException("Only Business accounts can create inventories");
        }

        if (inventory.getStatus() == null)
            inventory.setStatus(InventoryStatus.ACTIVE);
        if (inventory.getType() == null)
            inventory.setType(InventoryType.WAREHOUSE);
        inventory.setLastUpdated(LocalDateTime.now());
    }

    @HandleBeforeSave
    public void handleBeforeSave(Inventory inventory) {
        logger.info("Before updating inventory: {}", inventory);
        checkOwnership(inventory);

        // ONLY sync if products are loaded.
        // If not loaded, respect the totalStock value coming from the UI/request.
        if (inventory.getProducts() != null && !inventory.getProducts().isEmpty()) {
            inventory.syncTotalStock();
        }

        // Capacity logic
        if (inventory.getCapacity() != null && inventory.getCapacity() > 0) {
            if (inventory.getTotalStock() >= inventory.getCapacity()) {
                if (inventory.getStatus() != InventoryStatus.CLOSED
                        && inventory.getStatus() != InventoryStatus.MAINTENANCE) {
                    inventory.setStatus(InventoryStatus.FULL);
                    logger.info("Inventory {} reached capacity. Status set to FULL.", inventory.getId());
                }
            } else if (inventory.getStatus() == InventoryStatus.FULL) {
                inventory.setStatus(InventoryStatus.ACTIVE);
                logger.info("Inventory {} has space. Status restored to ACTIVE.", inventory.getId());
            }
        }
        inventory.setLastUpdated(LocalDateTime.now());
    }

    @HandleBeforeDelete
    public void handleBeforeDelete(Inventory inventory) {
        logger.info("Before deleting inventory: {}", inventory);
        checkOwnership(inventory);
    }

    private void checkOwnership(Inventory inventory) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        if (inventory.getBusiness() != null && !inventory.getBusiness().getUsername().equals(currentUsername)) {
            boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                throw new AccessDeniedException("You are not the owner of this inventory");
            }
        }
    }
}
