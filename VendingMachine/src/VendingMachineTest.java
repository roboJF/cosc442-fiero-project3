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

    @Test
    void testConstructorNoBalance(){
        double balance = machine.getBalance();

        assertEquals(0.0, balance, 0.0001);
    }

    @Test
    void testConstructorEmptySlots(){
        assertNull(machine.getItem("A"));
        assertNull(machine.getItem("B"));
        assertNull(machine.getItem("C"));
        assertNull(machine.getItem("D"));
    }

    //-------------------- addItem() --------------------

    @Test
    void testAddItem(){
        machine.addItem(coke, "A");

        assertSame(coke, machine.getItem("A"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"E", "a", "", "2", "BB", " "})
    void testAddItemInvalidCode(String code){
        assertThrows(VendingMachineException.class, () -> machine.addItem(coke, code));
    }

    @Test
    void testAddItemOccupied() {
        VendingMachineItem pepsi = new VendingMachineItem("Pepsi", 1.48);
        machine.addItem(coke, "B");

        assertThrows(VendingMachineException.class, () -> machine.addItem(pepsi, "B"));
        //this is to make sure that the item in the slot isnt replaced for whatever reason
        assertSame(coke, machine.getItem("B"));
    }

    @Test
    void testAddItemNullCode() {
        assertThrows(VendingMachineException.class, () -> machine.addItem(coke, null));
    }

    //-------------------- getItem() --------------------

    @Test
    void testGetItem(){
        machine.addItem(coke, "A");
        VendingMachineItem r = machine.getItem("A");
        assertSame(coke, r);
    }

    @Test
    void testGetItemEmpty(){
        VendingMachineItem r = machine.getItem("D");
        assertNull(r);
    }

    @Test
    void testGetItemInvalidCode(){
        assertThrows(VendingMachineException.class, () -> machine.getItem("c"));
    }

    @Test
    void testGetItemNullCode() {
        assertThrows(VendingMachineException.class, () -> machine.addItem(coke, null));
    }

    //-------------------- removeItem() --------------------

    @Test
    void testRemoveItem() {
        machine.addItem(coke, "A");
        VendingMachineItem remove = machine.removeItem("A");

        assertSame(coke, remove);
        assertNull(machine.getItem("A"));
    }

    @Test
    void testRemoveItemEmpty(){
        assertThrows(VendingMachineException.class, () -> machine.removeItem("A"));
    }

    @Test
    void testRemoveItemInvalidCode(){
        assertThrows(VendingMachineException.class, () -> machine.removeItem("F"));
    }

    @Test
    void testRemoveItemNullCode(){
        assertThrows(VendingMachineException.class, () -> machine.removeItem(null));
    }

    //-------------------- insertMoney() --------------------

    @Test
    void testInsertMoneyPositive(){
        machine.insertMoney(5.00);
        assertEquals(5.00, machine.getBalance(), 0.001);
    }

    @Test
    void testInsertMoneyMultiple(){
        machine.insertMoney(5.00);
        machine.insertMoney(3.00);
        machine.insertMoney(0.23);

        assertEquals(8.23, machine.getBalance(), 0.001);
    }

    @Test
    void testInsertMoneyZero(){
        machine.insertMoney(0.00);
        assertEquals(0.00, machine.getBalance(), 0.001);
    }

    @Test
    void testInsertMoneyNegative(){
        assertThrows(VendingMachineException.class, () -> machine.insertMoney(-5.00));
        assertEquals(0.00, machine.getBalance(), 0.001);
    }

    //-------------------- getBalance() --------------------

    @Test
    void testGetBalanceNew(){
        assertEquals(0.00, machine.getBalance(), 0.001);
    }

    @Test
    void testGetBalanceAfterInsertion(){
        machine.insertMoney(10.00);
        assertEquals(10.00, machine.getBalance(), 0.001);
    }

    //-------------------- makePurchase() --------------------

    @Test
    void testMakePurchase(){
        machine.addItem(coke, "C");
        machine.insertMoney(5.00);

        assertTrue(machine.makePurchase("C"));
        assertNull(machine.getItem("C"));
        assertEquals(3.51, machine.getBalance(), 0.001);
    }

    @Test 
    void testMakePurchaseInsufficient(){
        machine.addItem(coke, "C");
        machine.insertMoney(1);

        assertFalse(machine.makePurchase("C"));
        assertSame(coke, machine.getItem("C"));
        assertEquals(1.00, machine.getBalance(), 0.001);
    }

    @Test
    void testMakePurchaseExact(){
        machine.addItem(coke, "B");
        machine.insertMoney(1.49);

        assertTrue(machine.makePurchase("B"));
        assertNull(machine.getItem("B"));
        assertEquals(0.00, machine.getBalance(), 0.001);
    }

    @Test
    void testMakePurchaseInvalidCode(){
        assertThrows(VendingMachineException.class, () -> machine.makePurchase("E"));
    }

    @Test
    void testMakePurchaseEmpty(){
        machine.insertMoney(5.00);

        assertFalse(machine.makePurchase("D"));
        assertEquals(5.00, machine.getBalance(), 0.001);
    }

    @Test
    void testMakePurchaseNullCode(){
        assertThrows(VendingMachineException.class, () -> machine.makePurchase(null));
    }

    //-------------------- returnChange() --------------------

    @Test
    void testReturnChange(){
        machine.insertMoney(5.00);

        assertEquals(5.00, machine.returnChange(), 0.001);
        assertEquals(0.00, machine.getBalance(), 0.001);
    }

    @Test
    void testReturnChangeZero(){
        assertEquals(0.00, machine.returnChange(), 0.001);
        assertEquals(0.00, machine.getBalance(), 0.001);
    }
}
