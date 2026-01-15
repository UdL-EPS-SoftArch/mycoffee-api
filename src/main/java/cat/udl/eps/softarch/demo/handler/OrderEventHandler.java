package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Customer;
import cat.udl.eps.softarch.demo.domain.Order;
import cat.udl.eps.softarch.demo.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class OrderEventHandler {

    final Logger logger = LoggerFactory.getLogger(OrderEventHandler.class);

    final OrderRepository orderRepository;

    public OrderEventHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @HandleBeforeCreate
    public void handlePreCreate(Order order) {
        logger.info("Before creating: {}", order.toString());

        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        order.setCustomer(customer);
    }

    @HandleBeforeSave
    public void handlePreSave(Order order) {
        logger.info("Before updating: {}", order.toString());
    }
}
