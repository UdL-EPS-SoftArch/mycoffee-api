package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Business;
import cat.udl.eps.softarch.demo.domain.Inventory;
import cat.udl.eps.softarch.demo.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        
        // 1. Validació 
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Must be logged in to create inventory");
        }

        
        Object principal = auth.getPrincipal();

        if (principal instanceof Business) {
            Business business = (Business)Ql principal;
            inventory.setBusiness(business);
        } else {
            throw new AccessDeniedException("Only Business accounts can create inventories. You are: " + principal.getClass().getSimpleName());
        }
    }

    @HandleBeforeSave
    public void handleBeforeSave(Inventory inventory) {
        logger.info("Before updating inventory: {}", inventory);
        checkOwnership(inventory);
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
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (!isAdmin) {
                throw new AccessDeniedException("You can only modify your own inventory");
            }
        }
    }
}
