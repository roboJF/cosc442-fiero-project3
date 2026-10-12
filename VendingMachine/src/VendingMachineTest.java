import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class VendingMachineTest {
    private VendingMachine machine;
    private VendingMachineItem coke;

    @BeforeEach
    void setUp(){
        machine = new VendingMachine();
        coke = new VendingMachineItem("Coke", 1.49);
    }

    //-------------------- VendingMachine() --------------------

    // Verifies that a new machine starts with a zero balance.
    @Test
    void testConstructorNoBalance(){
        double balance = machine.getBalance();

        assertEquals(0.0, balance, 0.0001);
    }

    // Verifies that all four slots are empty in a new machine.
    @Test
    void testConstructorEmptySlots(){
        assertNull(machine.getItem("A"));
        assertNull(machine.getItem("B"));
        assertNull(machine.getItem("C"));
        assertNull(machine.getItem("D"));
    }

    //-------------------- addItem() --------------------

    // Verifies that adding an item stores that exact item in the requested slot.
    @Test
    void testAddItem(){
        machine.addItem(coke, "A");

        assertSame(coke, machine.getItem("A"));
    }

    // Verifies that each invalid slot code causes addItem to throw an exception.
    @ParameterizedTest
    @ValueSource(strings = {"E", "a", "", "2", "BB", " "})
    void testAddItemInvalidCode(String code){
        assertThrows(VendingMachineException.class, () -> machine.addItem(coke, code));
    }

    // Verifies that adding to an occupied slot throws and preserves the original item.
    @Test
    void testAddItemOccupied() {
        VendingMachineItem pepsi = new VendingMachineItem("Pepsi", 1.48);
        machine.addItem(coke, "B");

        assertThrows(VendingMachineException.class, () -> machine.addItem(pepsi, "B"));
        //this is to make sure that the item in the slot isnt replaced for whatever reason
        assertSame(coke, machine.getItem("B"));
    }

    // Verifies that addItem rejects a null slot code with an exception.
    @Test
    void testAddItemNullCode() {
        assertThrows(VendingMachineException.class, () -> machine.addItem(coke, null));
    }

    //-------------------- getItem() --------------------

    // Verifies that getItem returns the exact item stored in the requested slot.
    @Test
    void testGetItem(){
        machine.addItem(coke, "A");
        VendingMachineItem r = machine.getItem("A");
        assertSame(coke, r);
    }

    // Verifies that retrieving an item from an empty slot returns null.
    @Test
    void testGetItemEmpty(){
        VendingMachineItem r = machine.getItem("D");
        assertNull(r);
    }

    // Verifies that getItem rejects a lowercase slot code with an exception.
    @Test
    void testGetItemInvalidCode(){
        assertThrows(VendingMachineException.class, () -> machine.getItem("c"));
    }

    // Currently verifies that addItem rejects a null code, despite the getItem test name.
    @Test
    void testGetItemNullCode() {
        assertThrows(VendingMachineException.class, () -> machine.addItem(coke, null));
    }

    //-------------------- removeItem() --------------------

    // Verifies that removing an item returns that item and leaves its slot empty.
    @Test
    void testRemoveItem() {
        machine.addItem(coke, "A");
        VendingMachineItem remove = machine.removeItem("A");

        assertSame(coke, remove);
        assertNull(machine.getItem("A"));
    }

    // Verifies that removing an item from an empty slot throws an exception.
    @Test
    void testRemoveItemEmpty(){
        assertThrows(VendingMachineException.class, () -> machine.removeItem("A"));
    }

    // Verifies that removeItem rejects an invalid slot code with an exception.
    @Test
    void testRemoveItemInvalidCode(){
        assertThrows(VendingMachineException.class, () -> machine.removeItem("F"));
    }

    // Verifies that removeItem rejects a null slot code with an exception.
    @Test
    void testRemoveItemNullCode(){
        assertThrows(VendingMachineException.class, () -> machine.removeItem(null));
    }

    //-------------------- insertMoney() --------------------

    // Verifies that inserting a positive amount increases the balance by that amount.
    @Test
    void testInsertMoneyPositive(){
        machine.insertMoney(5.00);
        assertEquals(5.00, machine.getBalance(), 0.001);
    }

    // Verifies that multiple deposits accumulate in the balance, including cents.
    @Test
    void testInsertMoneyMultiple(){
        machine.insertMoney(5.00);
        machine.insertMoney(3.00);
        machine.insertMoney(0.23);

        assertEquals(8.23, machine.getBalance(), 0.001);
    }

    // Verifies that inserting zero is accepted and leaves the zero balance unchanged.
    @Test
    void testInsertMoneyZero(){
        machine.insertMoney(0.00);
        assertEquals(0.00, machine.getBalance(), 0.001);
    }

    // Verifies that a negative deposit throws and leaves the balance unchanged.
    @Test
    void testInsertMoneyNegative(){
        assertThrows(VendingMachineException.class, () -> machine.insertMoney(-5.00));
        assertEquals(0.00, machine.getBalance(), 0.001);
    }

    //-------------------- getBalance() --------------------

    // Verifies that getBalance returns zero before any money is inserted.
    @Test
    void testGetBalanceNew(){
        assertEquals(0.00, machine.getBalance(), 0.001);
    }

    // Verifies that getBalance reports the amount deposited into the machine.
    @Test
    void testGetBalanceAfterInsertion(){
        machine.insertMoney(10.00);
        assertEquals(10.00, machine.getBalance(), 0.001);
    }

    //-------------------- makePurchase() --------------------

    // Verifies that a funded purchase succeeds, removes the item, and deducts its price.
    @Test
    void testMakePurchase(){
        machine.addItem(coke, "C");
        machine.insertMoney(5.00);

        assertTrue(machine.makePurchase("C"));
        assertNull(machine.getItem("C"));
        assertEquals(3.51, machine.getBalance(), 0.001);
    }

    // Verifies that insufficient funds prevent a purchase and preserve the item and balance.
    @Test 
    void testMakePurchaseInsufficient(){
        machine.addItem(coke, "C");
        machine.insertMoney(1);

        assertFalse(machine.makePurchase("C"));
        assertSame(coke, machine.getItem("C"));
        assertEquals(1.00, machine.getBalance(), 0.001);
    }

    // Verifies that paying the exact price succeeds, removes the item, and leaves no balance.
    @Test
    void testMakePurchaseExact(){
        machine.addItem(coke, "B");
        machine.insertMoney(1.49);

        assertTrue(machine.makePurchase("B"));
        assertNull(machine.getItem("B"));
        assertEquals(0.00, machine.getBalance(), 0.001);
    }

    // Verifies that attempting a purchase with an invalid slot code throws an exception.
    @Test
    void testMakePurchaseInvalidCode(){
        assertThrows(VendingMachineException.class, () -> machine.makePurchase("E"));
    }

    // Verifies that purchasing from an empty slot fails without changing the balance.
    @Test
    void testMakePurchaseEmpty(){
        machine.insertMoney(5.00);

        assertFalse(machine.makePurchase("D"));
        assertEquals(5.00, machine.getBalance(), 0.001);
    }

    // Verifies that attempting a purchase with a null slot code throws an exception.
    @Test
    void testMakePurchaseNullCode(){
        assertThrows(VendingMachineException.class, () -> machine.makePurchase(null));
    }

    //-------------------- returnChange() --------------------

    // Verifies that returning change refunds the entire balance and resets it to zero.
    @Test
    void testReturnChange(){
        machine.insertMoney(5.00);

        assertEquals(5.00, machine.returnChange(), 0.001);
        assertEquals(0.00, machine.getBalance(), 0.001);
    }

    // Verifies that returning change from a zero balance returns zero and keeps it at zero.
    @Test
    void testReturnChangeZero(){
        assertEquals(0.00, machine.returnChange(), 0.001);
        assertEquals(0.00, machine.getBalance(), 0.001);
    }
}
