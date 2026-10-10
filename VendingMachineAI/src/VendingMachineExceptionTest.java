import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class VendingMachineExceptionTest {
    @Test
    void defaultConstructorHasNoMessage() {
        assertNull(new VendingMachineException().getMessage());
    }

    @Test
    void messageConstructorPreservesReason() {
        assertEquals("Out of stock", new VendingMachineException("Out of stock").getMessage());
    }

    @Test
    void messageConstructorAcceptsEmptyReason() {
        assertEquals("", new VendingMachineException("").getMessage());
    }
}
