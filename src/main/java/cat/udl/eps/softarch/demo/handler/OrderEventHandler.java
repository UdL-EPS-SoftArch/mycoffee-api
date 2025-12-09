package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Customer;
import cat.udl.eps.softarch.demo.domain.Order;
import cat.udl.eps.softarch.demo.domain.Loyalty;
import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.LoyaltyRepository;
import cat.udl.eps.softarch.demo.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class OrderEventHandler {

    final Logger logger = LoggerFactory.getLogger(OrderEventHandler.class);

    final OrderRepository orderRepository;

    @Autowired
    private LoyaltyRepository loyaltyRepository;

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

    @HandleBeforeCreate
    public void handleOrderCreate(Order order) {
        // Si el producto pertenece al programa de lealtad, sumar puntos
        if (order.getBasket() != null && order.getBasket().getProducts() != null) {
            Integer totalPoints = 0;

            for (Product product : order.getBasket().getProducts()) {
                if (product.isPartOfLoyaltyProgram() && product.getPointsGiven() != null) {
                    totalPoints += product.getPointsGiven();
                }
            }

            // Sumar puntos si es aplicable
            if (totalPoints > 0 && order.getCustomer() != null) {
                Loyalty loyalty = loyaltyRepository.findByCustomerOrderByStartDateDesc(order.getCustomer())
                        .stream()
                        .findFirst()
                        .orElse(null);

                if (loyalty != null) {
                    loyalty.setAccumulatedPoints(loyalty.getAccumulatedPoints() + totalPoints);
                    loyaltyRepository.save(loyalty);
                }
            }
        }
    }
}
