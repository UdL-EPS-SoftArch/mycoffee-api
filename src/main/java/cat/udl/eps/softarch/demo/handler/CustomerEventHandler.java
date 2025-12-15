package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Customer;
import cat.udl.eps.softarch.demo.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterLinkSave;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class CustomerEventHandler {

    final Logger logger = LoggerFactory.getLogger(Customer.class);

    final CustomerRepository customerRepository;

    public CustomerEventHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @HandleBeforeCreate
    public void handleCustomerPreCreate(Customer customer) {
        logger.info("Before creating: {}", customer.toString());
    }

    @HandleBeforeSave
    public void handleCustomerPreSave(Customer customer) {
        logger.info("Before updating: {}", customer.toString());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() &&
                !auth.getName().equals("anonymousUser")) {

            String currentUsername = auth.getName();
            String customerUsername = customer.getId();

            // Si l'usuari autenticat NO és el propietari del perfil, llançar excepció
            if (!currentUsername.equals(customerUsername)) {
                logger.warn("User {} tried to update customer {}", currentUsername, customerUsername);
                throw new AccessDeniedException("You can only update your own profile");
            }
        }
    }

    @HandleBeforeDelete
    public void handleCustomerPreDelete(Customer customer) {
        logger.info("Before deleting: {}", customer.toString());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Si no hay autenticación o el usuario es anónimo → no permitido
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new AccessDeniedException("You must be logged in to delete a customer");
        }

        String currentUsername = auth.getName();
        String customerUsername = customer.getId();

        // Solo el mismo usuario puede borrarse
        if (!currentUsername.equals(customerUsername)) {
            logger.warn("User {} tried to delete customer {}", currentUsername, customerUsername);
            throw new AccessDeniedException("You can only delete your own profile");
        }
    }

    @HandleBeforeLinkSave
    public void handleCustomerPreLinkSave(Customer customer, Object o) {
        logger.info("Before linking: {} to {}", customer.toString(), o.toString());
    }

    @HandleAfterCreate
    public void handleCustomerPostCreate(Customer customer) {
        // No need to call password encode, it is already done by the UserEventHandler
    }

    @HandleAfterSave
    public void handleCustomerPostSave(Customer customer) {
        logger.info("After updating: {}", customer.toString());
        if (customer.isPasswordReset()) {
            customer.encodePassword();
        }
        customerRepository.save(customer);
    }

    @HandleAfterDelete
    public void handleCustomerPostDelete(Customer customer) {
        logger.info("After deleting: {}", customer.toString());
    }

    @HandleAfterLinkSave
    public void handleCustomerPostLinkSave(Customer customer, Object o) {
        logger.info("After linking: {} to {}", customer.toString(), o.toString());
    }
}
