import static org.junit.Assert.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


public class VendingMachineItemTest {

    //-------------------- VendingMachineItem() --------------------

    @Test
    void testVendingMachineItemConstructor(){
        VendingMachineItem coke = new VendingMachineItem("Coke", 1.49);

        assertEquals("Coke", coke.getName());
        assertEquals(1.49, coke.getPrice(), 0.001);
    }

    @Test
    void testVendingMachineItemConstructorNegative(){
        assertThrows(VendingMachineException.class, () -> new VendingMachineItem("Coke", -1.49));
    }

    @Test
    void testVendingMachineItemConstructorZero(){
        VendingMachineItem coke = new VendingMachineItem("Expired Coke", 0.00);

        assertEquals("Expired Coke", coke.getName());
        assertEquals(0.00, coke.getPrice(), 0.001);
    }

    //-------------------- getName() --------------------

    @Test
    void testGetName(){
        VendingMachineItem coke = new VendingMachineItem("Coke", 1.49);

        assertEquals("Coke", coke.getName());
    }

    //-------------------- getPrice() --------------------

    @Test
    void testGetPrice(){
        VendingMachineItem coke = new VendingMachineItem("Coke", 1.49);

        assertEquals(1.49, coke.getPrice(), 0.001);
    }

    @Test
    void testGetPriceZero(){
        VendingMachineItem coke = new VendingMachineItem("Still Expired Coke", 0.00);

        assertEquals(0.00, coke.getPrice(), 0.001);
    }
}
