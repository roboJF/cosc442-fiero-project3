import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class VendingMachineExceptionTest {
    // Checks the no-argument exception has no message.
    @Test
    void defaultConstructorHasNoMessage() {
        assertNull(new VendingMachineException().getMessage());
    }

    // Checks the exception retains the reason passed to its constructor.
    @Test
    void messageConstructorPreservesReason() {
        assertEquals("Out of stock", new VendingMachineException("Out of stock").getMessage());
    }

    // Checks an empty reason remains an empty message.
    @Test
    void messageConstructorAcceptsEmptyReason() {
        assertEquals("", new VendingMachineException("").getMessage());
    }
}
