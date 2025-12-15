package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class CategoryEventHandler {

    final Logger logger = LoggerFactory.getLogger(Category.class);

    @HandleBeforeCreate
    public void handleCategoryPreCreate(Category category) throws AccessDeniedException {
        logger.info("Before creating category: {}", category.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required to create categories");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            logger.warn("User {} attempted to create category without admin privileges",
                    authentication.getName());
            throw new AccessDeniedException("Only administrators can create categories");
        }
    }
}
