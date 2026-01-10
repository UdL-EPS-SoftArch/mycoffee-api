package cat.udl.eps.softarch.demo.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    void testInventoryFields() {
        Inventory inventory = new Inventory();
        inventory.setStatus(InventoryStatus.ACTIVE);
        inventory.setType(InventoryType.FRIDGE);
        inventory.setCapacity(100);

        assertEquals(InventoryStatus.ACTIVE, inventory.getStatus());
        assertEquals(InventoryType.FRIDGE, inventory.getType());
        assertEquals(100, inventory.getCapacity());
    }
}
