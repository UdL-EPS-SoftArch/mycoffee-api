package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Business;
import cat.udl.eps.softarch.demo.repository.BusinessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class BusinessEventHandler {

    private final Logger logger = LoggerFactory.getLogger(BusinessEventHandler.class);
    private final BusinessRepository businessRepository;

    public BusinessEventHandler(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    @HandleBeforeCreate
    public void handleBeforeCreate(Business business) {
        logger.info("Before creating business: {}", business);
    }

    @HandleBeforeSave
    public void handleBeforeSave(Business business) {
        logger.info("Before updating business: {}", business);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {

            String currentUsername = auth.getName();
            String ownerId = business.getId();

            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin && !currentUsername.equals(ownerId)) {
                logger.warn("User {} tried to modify business {}", currentUsername, ownerId);
                throw new AccessDeniedException("You can only update your own business");
            }
        }
    }

    @HandleBeforeDelete
    public void handleBeforeDelete(Business business) {
        logger.info("Before deleting business: {}", business);
    }

    @HandleBeforeLinkSave
    public void handleBeforeLinkSave(Business business, Object linked) {
        logger.info("Before linking: {} to {}", business, linked);
    }

    @HandleAfterCreate
    public void handleAfterCreate(Business business) {
        logger.info("After creating business: {}", business);
        businessRepository.save(business);
    }

    @HandleAfterSave
    public void handleAfterSave(Business business) {
        logger.info("After updating business: {}", business);
        businessRepository.save(business);
    }

    @HandleAfterDelete
    public void handleAfterDelete(Business business) {
        logger.info("After deleting business: {}", business);
    }

    @HandleAfterLinkSave
    public void handleAfterLinkSave(Business business, Object linked) {
        logger.info("After linking: {} to {}", business, linked);
    }
}
