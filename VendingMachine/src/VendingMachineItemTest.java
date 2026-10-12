import static org.junit.Assert.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


public class VendingMachineItemTest {

    //-------------------- VendingMachineItem() --------------------

    // Verifies that the constructor stores the supplied item name and positive price.
    @Test
    void testVendingMachineItemConstructor(){
        VendingMachineItem coke = new VendingMachineItem("Coke", 1.49);

        assertEquals("Coke", coke.getName());
        assertEquals(1.49, coke.getPrice(), 0.001);
    }

    // Verifies that constructing an item with a negative price throws an exception.
    @Test
    void testVendingMachineItemConstructorNegative(){
        assertThrows(VendingMachineException.class, () -> new VendingMachineItem("Coke", -1.49));
    }

    // Verifies that the constructor accepts a zero price and preserves the item name.
    @Test
    void testVendingMachineItemConstructorZero(){
        VendingMachineItem coke = new VendingMachineItem("Expired Coke", 0.00);

        assertEquals("Expired Coke", coke.getName());
        assertEquals(0.00, coke.getPrice(), 0.001);
    }

    //-------------------- getName() --------------------

    // Verifies that getName returns the name supplied when the item was created.
    @Test
    void testGetName(){
        VendingMachineItem coke = new VendingMachineItem("Coke", 1.49);

        assertEquals("Coke", coke.getName());
    }

    //-------------------- getPrice() --------------------

    // Verifies that getPrice returns the positive price supplied at construction.
    @Test
    void testGetPrice(){
        VendingMachineItem coke = new VendingMachineItem("Coke", 1.49);

        assertEquals(1.49, coke.getPrice(), 0.001);
    }

    // Verifies that getPrice returns zero for an item created with a zero price.
    @Test
    void testGetPriceZero(){
        VendingMachineItem coke = new VendingMachineItem("Still Expired Coke", 0.00);

        assertEquals(0.00, coke.getPrice(), 0.001);
    }
}
