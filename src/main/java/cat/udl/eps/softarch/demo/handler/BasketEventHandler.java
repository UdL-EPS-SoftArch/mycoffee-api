package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Basket;
import cat.udl.eps.softarch.demo.domain.Loyalty;
import cat.udl.eps.softarch.demo.domain.Product;
import cat.udl.eps.softarch.demo.repository.LoyaltyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class BasketEventHandler {

    @Autowired
    private LoyaltyRepository loyaltyRepository;

    @HandleBeforeCreate
    public void handleBasketCreate(Basket basket) {
        // Calcular puntos a restar si el basket contiene productos canjeables
        if (basket.getProducts() != null && !basket.getProducts().isEmpty()) {
            Integer totalPointsCost = 0;
            
            for (Product product : basket.getProducts()) {
                if (product.isPartOfLoyaltyProgram() && product.getPointsCost() != null) {
                    totalPointsCost += product.getPointsCost();
                }
            }
            
            // Si hay puntos a restar, validar y restar de Loyalty
            if (totalPointsCost > 0 && basket.getCustomer() != null) {
                Loyalty loyalty = loyaltyRepository.findByCustomerOrderByStartDateDesc(basket.getCustomer())
                        .stream()
                        .findFirst()
                        .orElse(null);
                
                if (loyalty != null) {
                    // Validar que hay puntos suficientes
                    if (loyalty.getAccumulatedPoints() >= totalPointsCost) {
                        loyalty.setAccumulatedPoints(loyalty.getAccumulatedPoints() - totalPointsCost);
                        loyaltyRepository.save(loyalty);
                    } else {
                        throw new IllegalArgumentException(
                            "Insufficient loyalty points. Required: " + totalPointsCost + 
                            ", Available: " + loyalty.getAccumulatedPoints()
                        );
                    }
                }
            }
        }
    }
}