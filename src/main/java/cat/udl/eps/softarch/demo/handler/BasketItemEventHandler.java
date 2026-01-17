package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.BasketItem;
import cat.udl.eps.softarch.demo.repository.BasketRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class BasketItemEventHandler {

    private final BasketRepository basketRepository;

    public BasketItemEventHandler(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    @HandleBeforeCreate
    @HandleBeforeSave
    public void handleBasketItemSave(BasketItem item) {
        if (item.getBasket() != null) {
            basketRepository.save(item.getBasket()); // Updates basket updatedAt
        }
    }

    @HandleBeforeDelete
    public void handleBasketItemDelete(BasketItem item) {
        if (item.getBasket() != null) {
            basketRepository.save(item.getBasket());
        }
    }
}
