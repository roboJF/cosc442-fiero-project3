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

    // Gives each test a fresh machine so its result does not depend on other tests.
    @BeforeEach
    void setUp() {
        machine = new VendingMachine();
    }

    // Checks the initial balance and confirms all four slots start empty.
    @Test
    void newMachineHasFourEmptySlotsAndNoBalance() {
        assertEquals(4, VendingMachine.NUM_SLOTS);
        for (String code : new String[] { "A", "B", "C", "D" }) {
            assertNull(machine.getItem(code));
        }
        assertEquals(0.0, machine.getBalance());
    }

    // Checks that each valid slot stores and returns the exact item added to it.
    @ParameterizedTest
    @ValueSource(strings = { "A", "B", "C", "D" })
    void addAndGetItemInEverySlot(String code) {
        VendingMachineItem item = new VendingMachineItem(code, 1.25);

        machine.addItem(item, code);

        assertSame(item, machine.getItem(code));
        assertEquals(0.0, machine.getBalance());
    }

    // Checks that stocking one slot does not fill or change another slot.
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

    // Checks that every occupied slot rejects a second item without replacing the first.
    @ParameterizedTest
    @ValueSource(strings = { "A", "B", "C", "D" })
    void addingToOccupiedSlotThrowsAndKeepsOriginalItem(String code) {
        VendingMachineItem original = new VendingMachineItem("Chips", 1.00);
        machine.addItem(original, code);

        VendingMachineException error = assertThrows(VendingMachineException.class,
                () -> machine.addItem(new VendingMachineItem("Candy", 2.00), code));

        assertEquals("Slot " + code + " already occupied", error.getMessage());
        assertSame(original, machine.getItem(code));
    }

    // Checks that null, empty, and malformed codes fail for every slot operation
    // without changing an existing item or the balance.
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "a", "E", "AA", " A", "A " })
    void invalidCodesAreRejectedByEverySlotOperation(String code) {
        VendingMachineItem item = new VendingMachineItem("Chips", 1.00);
        machine.addItem(item, "A");
        machine.insertMoney(2.00);

        assertEquals("Invalid code for vending machine item",
                assertThrows(VendingMachineException.class, () -> machine.addItem(item, code)).getMessage());
        assertEquals("Invalid code for vending machine item",
                assertThrows(VendingMachineException.class, () -> machine.getItem(code)).getMessage());
        assertEquals("Invalid code for vending machine item",
                assertThrows(VendingMachineException.class, () -> machine.removeItem(code)).getMessage());
        assertEquals("Invalid code for vending machine item",
                assertThrows(VendingMachineException.class, () -> machine.makePurchase(code)).getMessage());
        assertSame(item, machine.getItem("A"));
        assertEquals(2.00, machine.getBalance());
    }

    // Checks that removing an item returns that same object and empties its slot.
    @ParameterizedTest
    @ValueSource(strings = { "A", "B", "C", "D" })
    void removeReturnsItemAndEmptiesSlot(String code) {
        VendingMachineItem item = new VendingMachineItem("Snack", 0.75);
        machine.addItem(item, code);

        assertSame(item, machine.removeItem(code));
        assertNull(machine.getItem(code));
        assertEquals(0.0, machine.getBalance());
    }

    // Checks that removing from any empty slot reports the correct error.
    @ParameterizedTest
    @ValueSource(strings = { "A", "B", "C", "D" })
    void removingEmptySlotThrowsAndLeavesItEmpty(String code) {
        VendingMachineException error = assertThrows(VendingMachineException.class,
                () -> machine.removeItem(code));

        assertEquals("Slot " + code + " is empty -- cannot remove item", error.getMessage());
        assertNull(machine.getItem(code));
    }

    // Checks that a slot can accept a new item after its previous item is removed.
    @Test
    void removedSlotCanBeRestocked() {
        machine.addItem(new VendingMachineItem("Old", 1.00), "A");
        machine.removeItem("A");
        VendingMachineItem replacement = new VendingMachineItem("New", 2.00);

        machine.addItem(replacement, "A");

        assertSame(replacement, machine.getItem("A"));
    }

    // Checks that deposits add together and reading the balance does not consume it.
    @Test
    void insertingMoneyAccumulatesAndReadingBalanceDoesNotChangeIt() {
        machine.insertMoney(1.25);
        machine.insertMoney(0.75);

        assertEquals(2.00, machine.getBalance(), 0.000001);
        assertEquals(2.00, machine.getBalance(), 0.000001);
    }

    // Checks that a zero deposit is allowed and leaves the balance unchanged.
    @Test
    void insertingZeroDoesNotChangeBalance() {
        machine.insertMoney(1.00);
        machine.insertMoney(0.00);

        assertEquals(1.00, machine.getBalance());
    }

    // Checks the smallest representable positive deposit is accepted.
    @Test
    void smallestPositiveDepositIsAccepted() {
        machine.insertMoney(Double.MIN_VALUE);

        assertEquals(Double.MIN_VALUE, machine.getBalance());
    }

    // Checks negative deposits, including the value nearest zero, are rejected
    // without changing money already inserted.
    @ParameterizedTest
    @ValueSource(doubles = { -1.0, -0.01, -0.000001, -Double.MIN_VALUE })
    void negativeDepositThrowsAndPreservesBalance(double amount) {
        machine.insertMoney(2.00);

        VendingMachineException error = assertThrows(VendingMachineException.class,
                () -> machine.insertMoney(amount));

        assertEquals("Invalid amount.  Amount must be >= 0", error.getMessage());
        assertEquals(2.00, machine.getBalance());
    }

    // Checks an item can be bought for its exact price, leaving no item or balance.
    @Test
    void exactBalancePurchasesAndRemovesItem() {
        machine.addItem(new VendingMachineItem("Chips", 1.50), "A");
        machine.insertMoney(1.50);

        assertTrue(machine.makePurchase("A"));
        assertNull(machine.getItem("A"));
        assertEquals(0.00, machine.getBalance());
    }

    // Checks a purchase succeeds with extra money and leaves the correct balance.
    @Test
    void purchaseWithExtraMoneyLeavesCorrectChange() {
        machine.addItem(new VendingMachineItem("Water", 1.25), "D");
        machine.insertMoney(2.00);

        assertTrue(machine.makePurchase("D"));
        assertEquals(0.75, machine.getBalance(), 0.000001);
        assertNull(machine.getItem("D"));
    }

    // Checks insufficient money leaves both the item and deposited money intact.
    @Test
    void insufficientBalanceLeavesItemAndMoneyUntouched() {
        VendingMachineItem item = new VendingMachineItem("Chips", 1.50);
        machine.addItem(item, "B");
        machine.insertMoney(1.49);

        assertFalse(machine.makePurchase("B"));
        assertSame(item, machine.getItem("B"));
        assertEquals(1.49, machine.getBalance(), 0.000001);
    }

    // Checks the floating-point value just below the price fails, then succeeds
    // after enough additional money is inserted.
    @Test
    void purchaseJustBelowPriceFailsAndCanSucceedAfterAnotherDeposit() {
        VendingMachineItem item = new VendingMachineItem("Water", 1.00);
        machine.addItem(item, "A");
        double justBelowPrice = Math.nextDown(1.00);
        machine.insertMoney(justBelowPrice);

        assertFalse(machine.makePurchase("A"));
        assertSame(item, machine.getItem("A"));
        assertEquals(justBelowPrice, machine.getBalance());

        machine.insertMoney(Math.ulp(1.00));
        assertTrue(machine.makePurchase("A"));
        assertNull(machine.getItem("A"));
        assertEquals(0.00, machine.getBalance());
    }

    // Checks buying one item from a full machine leaves the other three untouched.
    @Test
    void buyingOneOfFourItemsDoesNotAffectOtherSlots() {
        VendingMachineItem[] items = {
                new VendingMachineItem("A", 0.50),
                new VendingMachineItem("B", 0.75),
                new VendingMachineItem("C", 1.00),
                new VendingMachineItem("D", 1.25)
        };
        String[] codes = { "A", "B", "C", "D" };
        for (int i = 0; i < codes.length; i++) {
            machine.addItem(items[i], codes[i]);
        }
        machine.insertMoney(2.00);

        assertTrue(machine.makePurchase("B"));

        assertSame(items[0], machine.getItem("A"));
        assertNull(machine.getItem("B"));
        assertSame(items[2], machine.getItem("C"));
        assertSame(items[3], machine.getItem("D"));
        assertEquals(1.25, machine.getBalance(), 0.000001);
    }

    // Checks selecting an empty slot fails without consuming the balance.
    @Test
    void emptySlotCannotBePurchased() {
        machine.insertMoney(3.00);

        assertFalse(machine.makePurchase("C"));
        assertEquals(3.00, machine.getBalance());
    }

    // Checks a zero-price item can be purchased with an empty balance.
    @Test
    void freeItemCanBePurchasedWithoutMoney() {
        machine.addItem(new VendingMachineItem("Sample", 0.00), "C");

        assertTrue(machine.makePurchase("C"));
        assertNull(machine.getItem("C"));
        assertEquals(0.00, machine.getBalance());
    }

    // Checks a purchased item cannot be bought again while its slot is empty.
    @Test
    void successfulPurchaseCannotBeRepeatedUntilRestocked() {
        machine.addItem(new VendingMachineItem("Candy", 0.50), "B");
        machine.insertMoney(1.00);

        assertTrue(machine.makePurchase("B"));
        assertFalse(machine.makePurchase("B"));
        assertEquals(0.50, machine.getBalance(), 0.000001);
    }

    // Checks two purchases use the remaining balance from the first purchase.
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

    // Checks returning change pays the full balance and resets it to zero.
    @Test
    void returnChangePaysBalanceAndResetsIt() {
        machine.insertMoney(2.50);

        assertEquals(2.50, machine.returnChange());
        assertEquals(0.00, machine.getBalance());
        assertEquals(0.00, machine.returnChange());
    }

    // Checks change after a purchase contains only the unspent money.
    @Test
    void returnChangeAfterPurchaseReturnsOnlyTheRemainder() {
        machine.addItem(new VendingMachineItem("Chips", 1.25), "A");
        machine.insertMoney(2.00);
        assertTrue(machine.makePurchase("A"));

        assertEquals(0.75, machine.returnChange(), 0.000001);
        assertEquals(0.00, machine.getBalance());
    }

    // Checks returning change does not remove stocked items or make them free.
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
