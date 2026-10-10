import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class VendingMachineTest {
    private VendingMachine machine;

    @BeforeEach
    void setUp() {
        machine = new VendingMachine();
    }

    @Test
    void newMachineHasFourEmptySlotsAndNoBalance() {
        assertEquals(4, VendingMachine.NUM_SLOTS);
        for (String code : new String[] { "A", "B", "C", "D" }) {
            assertNull(machine.getItem(code));
        }
        assertEquals(0.0, machine.getBalance());
    }

    @ParameterizedTest
    @ValueSource(strings = { "A", "B", "C", "D" })
    void addAndGetItemInEverySlot(String code) {
        VendingMachineItem item = new VendingMachineItem(code, 1.25);

        machine.addItem(item, code);

        assertSame(item, machine.getItem(code));
        assertEquals(0.0, machine.getBalance());
    }

    @Test
    void slotsAreIndependent() {
        VendingMachineItem first = new VendingMachineItem("Chips", 1.00);
        VendingMachineItem last = new VendingMachineItem("Water", 1.50);
        machine.addItem(first, "A");
        machine.addItem(last, "D");

        assertSame(first, machine.getItem("A"));
        assertSame(last, machine.getItem("D"));
        assertNull(machine.getItem("B"));
        assertNull(machine.getItem("C"));
    }

    @Test
    void addingToOccupiedSlotThrowsAndKeepsOriginalItem() {
        VendingMachineItem original = new VendingMachineItem("Chips", 1.00);
        machine.addItem(original, "B");

        VendingMachineException error = assertThrows(VendingMachineException.class,
                () -> machine.addItem(new VendingMachineItem("Candy", 2.00), "B"));

        assertEquals("Slot B already occupied", error.getMessage());
        assertSame(original, machine.getItem("B"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "a", "E", "AA", " A", "A " })
    void invalidCodesAreRejectedByEverySlotOperation(String code) {
        VendingMachineItem item = new VendingMachineItem("Chips", 1.00);
        assertEquals("Invalid code for vending machine item",
                assertThrows(VendingMachineException.class, () -> machine.addItem(item, code)).getMessage());
        assertEquals("Invalid code for vending machine item",
                assertThrows(VendingMachineException.class, () -> machine.getItem(code)).getMessage());
        assertEquals("Invalid code for vending machine item",
                assertThrows(VendingMachineException.class, () -> machine.removeItem(code)).getMessage());
        assertEquals("Invalid code for vending machine item",
                assertThrows(VendingMachineException.class, () -> machine.makePurchase(code)).getMessage());
        assertNull(machine.getItem("A"));
        assertEquals(0.0, machine.getBalance());
    }

    @ParameterizedTest
    @ValueSource(strings = { "A", "B", "C", "D" })
    void removeReturnsItemAndEmptiesSlot(String code) {
        VendingMachineItem item = new VendingMachineItem("Snack", 0.75);
        machine.addItem(item, code);

        assertSame(item, machine.removeItem(code));
        assertNull(machine.getItem(code));
        assertEquals(0.0, machine.getBalance());
    }

    @Test
    void removingEmptySlotThrowsAndLeavesItEmpty() {
        VendingMachineException error = assertThrows(VendingMachineException.class,
                () -> machine.removeItem("C"));

        assertEquals("Slot C is empty -- cannot remove item", error.getMessage());
        assertNull(machine.getItem("C"));
    }

    @Test
    void removedSlotCanBeRestocked() {
        machine.addItem(new VendingMachineItem("Old", 1.00), "A");
        machine.removeItem("A");
        VendingMachineItem replacement = new VendingMachineItem("New", 2.00);

        machine.addItem(replacement, "A");

        assertSame(replacement, machine.getItem("A"));
    }

    @Test
    void insertingMoneyAccumulatesAndReadingBalanceDoesNotChangeIt() {
        machine.insertMoney(1.25);
        machine.insertMoney(0.75);

        assertEquals(2.00, machine.getBalance(), 0.000001);
        assertEquals(2.00, machine.getBalance(), 0.000001);
    }

    @Test
    void insertingZeroDoesNotChangeBalance() {
        machine.insertMoney(1.00);
        machine.insertMoney(0.00);

        assertEquals(1.00, machine.getBalance());
    }

    @ParameterizedTest
    @ValueSource(doubles = { -1.0, -0.01, -0.000001 })
    void negativeDepositThrowsAndPreservesBalance(double amount) {
        machine.insertMoney(2.00);

        VendingMachineException error = assertThrows(VendingMachineException.class,
                () -> machine.insertMoney(amount));

        assertEquals("Invalid amount.  Amount must be >= 0", error.getMessage());
        assertEquals(2.00, machine.getBalance());
    }

    @Test
    void exactBalancePurchasesAndRemovesItem() {
        machine.addItem(new VendingMachineItem("Chips", 1.50), "A");
        machine.insertMoney(1.50);

        assertTrue(machine.makePurchase("A"));
        assertNull(machine.getItem("A"));
        assertEquals(0.00, machine.getBalance());
    }

    @Test
    void purchaseWithExtraMoneyLeavesCorrectChange() {
        machine.addItem(new VendingMachineItem("Water", 1.25), "D");
        machine.insertMoney(2.00);

        assertTrue(machine.makePurchase("D"));
        assertEquals(0.75, machine.getBalance(), 0.000001);
        assertNull(machine.getItem("D"));
    }

    @Test
    void insufficientBalanceLeavesItemAndMoneyUntouched() {
        VendingMachineItem item = new VendingMachineItem("Chips", 1.50);
        machine.addItem(item, "B");
        machine.insertMoney(1.49);

        assertFalse(machine.makePurchase("B"));
        assertSame(item, machine.getItem("B"));
        assertEquals(1.49, machine.getBalance(), 0.000001);
    }

    @Test
    void emptySlotCannotBePurchased() {
        machine.insertMoney(3.00);

        assertFalse(machine.makePurchase("C"));
        assertEquals(3.00, machine.getBalance());
    }

    @Test
    void freeItemCanBePurchasedWithoutMoney() {
        machine.addItem(new VendingMachineItem("Sample", 0.00), "C");

        assertTrue(machine.makePurchase("C"));
        assertNull(machine.getItem("C"));
        assertEquals(0.00, machine.getBalance());
    }

    @Test
    void successfulPurchaseCannotBeRepeatedUntilRestocked() {
        machine.addItem(new VendingMachineItem("Candy", 0.50), "B");
        machine.insertMoney(1.00);

        assertTrue(machine.makePurchase("B"));
        assertFalse(machine.makePurchase("B"));
        assertEquals(0.50, machine.getBalance(), 0.000001);
    }

    @Test
    void sequentialPurchasesUseRemainingBalance() {
        machine.addItem(new VendingMachineItem("Chips", 1.25), "A");
        machine.addItem(new VendingMachineItem("Water", 0.75), "D");
        machine.insertMoney(2.00);

        assertTrue(machine.makePurchase("A"));
        assertTrue(machine.makePurchase("D"));
        assertEquals(0.00, machine.getBalance(), 0.000001);
        assertNull(machine.getItem("A"));
        assertNull(machine.getItem("D"));
    }

    @Test
    void returnChangePaysBalanceAndResetsIt() {
        machine.insertMoney(2.50);

        assertEquals(2.50, machine.returnChange());
        assertEquals(0.00, machine.getBalance());
        assertEquals(0.00, machine.returnChange());
    }

    @Test
    void returningChangeDoesNotRemoveItems() {
        VendingMachineItem item = new VendingMachineItem("Water", 1.00);
        machine.addItem(item, "A");
        machine.insertMoney(0.50);

        assertEquals(0.50, machine.returnChange());
        assertSame(item, machine.getItem("A"));
        assertFalse(machine.makePurchase("A"));
    }
}
