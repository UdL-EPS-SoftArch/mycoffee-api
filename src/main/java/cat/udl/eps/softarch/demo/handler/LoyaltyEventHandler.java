package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Loyalty;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class LoyaltyEventHandler {

    @HandleBeforeCreate
    public void handleLoyaltyCreate(Loyalty loyalty) {
        if (loyalty.getAccumulatedPoints() == null) {
            loyalty.setAccumulatedPoints(0);
        }
    }
}