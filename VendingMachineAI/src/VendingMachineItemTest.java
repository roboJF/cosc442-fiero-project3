import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class VendingMachineItemTest {
    @Test
    void constructorStoresNameAndPositivePrice() {
        VendingMachineItem item = new VendingMachineItem("Chips", 1.25);

        assertEquals("Chips", item.getName());
        assertEquals(1.25, item.getPrice());
    }

    @Test
    void zeroPriceIsAccepted() {
        VendingMachineItem item = new VendingMachineItem("Sample", 0.00);

        assertEquals(0.00, item.getPrice());
    }

    @Test
    void smallPositivePriceIsAccepted() {
        assertEquals(Double.MIN_VALUE, new VendingMachineItem("Sample", Double.MIN_VALUE).getPrice());
    }

    @ParameterizedTest
    @ValueSource(doubles = { -1.00, -0.01, -0.000001, -Double.MIN_VALUE })
    void negativePriceThrows(double price) {
        VendingMachineException error = assertThrows(VendingMachineException.class,
                () -> new VendingMachineItem("Chips", price));

        assertEquals("Price cannot be less than zero", error.getMessage());
    }

    @Test
    void nullNameIsStoredAsGiven() {
        assertNull(new VendingMachineItem(null, 1.00).getName());
    }
}
