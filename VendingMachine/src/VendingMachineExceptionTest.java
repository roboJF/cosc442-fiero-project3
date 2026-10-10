import static org.junit.Assert.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


public class VendingMachineExceptionTest {
    
    //-------------------- VendingMachineException --------------------

    @Test 
    void testVendingMachineException() {
        VendingMachineException ex = new VendingMachineException();
        assertNotNull(ex);
    }

    @Test
    void testVendingMachineExceptionMessage(){
        String message = "You feel as if something truly terrible has happened...";
        VendingMachineException ex = new VendingMachineException(message);

        assertEquals(message, ex.getMessage());
    }

    @Test
    void testVendingMachineExceptionEmpty(){
        //You feel as if nothing has happened...
        VendingMachineException ex = new VendingMachineException("");

        assertEquals("", ex.getMessage());
    }
}
