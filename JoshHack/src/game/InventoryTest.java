package game;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class InventoryTest {

    private Item item(String name) {
        return new Item('!', Color.WHITE, name, name);
    }

    @Test
    void newInventoryHasRequestedCapacityAndEmptySlots() {
        Inventory inventory = new Inventory(3);

        assertAll(
                () -> assertEquals(3, inventory.getItems().length),
                () -> assertArrayEquals(new Item[3], inventory.getItems()),
                () -> assertFalse(inventory.isFull()));
    }

    @Test
    void zeroCapacityInventoryIsFullAndCannotStoreAnItem() {
        Inventory inventory = new Inventory(0);
        Item item = item("potion");

        inventory.add(item);

        assertAll(
                () -> assertEquals(0, inventory.getItems().length),
                () -> assertTrue(inventory.isFull()),
                () -> assertFalse(inventory.contains(item)));
    }

    @Test
    void negativeCapacityIsRejected() {
        assertThrows(NegativeArraySizeException.class, () -> new Inventory(-1));
    }

    @Test
    void getReturnsTheItemAtEachOccupiedIndexAndNullAtEmptyIndexes() {
        Inventory inventory = new Inventory(3);
        Item first = item("first");
        Item second = item("second");
        inventory.add(first);
        inventory.add(second);

        assertAll(
                () -> assertSame(first, inventory.get(0)),
                () -> assertSame(second, inventory.get(1)),
                () -> assertNull(inventory.get(2)));
    }

    @Test
    void getRejectsIndexesOutsideTheInventory() {
        Inventory inventory = new Inventory(2);

        assertAll(
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> inventory.get(-1)),
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> inventory.get(2)));
    }

    @Test
    void addFillsTheFirstAvailableSlot() {
        Inventory inventory = new Inventory(3);
        Item first = item("first");
        Item second = item("second");
        inventory.add(first);
        inventory.add(second);

        assertArrayEquals(new Item[] { first, second, null }, inventory.getItems());
    }

    @Test
    void addingToAFullInventoryLeavesExistingItemsUntouched() {
        Inventory inventory = new Inventory(2);
        Item first = item("first");
        Item second = item("second");
        Item extra = item("extra");
        inventory.add(first);
        inventory.add(second);

        inventory.add(extra);

        assertAll(
                () -> assertArrayEquals(new Item[] { first, second }, inventory.getItems()),
                () -> assertFalse(inventory.contains(extra)),
                () -> assertTrue(inventory.isFull()));
    }

    @Test
    void addingNullLeavesAnEmptySlotAvailable() {
        Inventory inventory = new Inventory(1);

        inventory.add(null);

        assertAll(
                () -> assertNull(inventory.get(0)),
                () -> assertFalse(inventory.isFull()));
    }

    @Test
    void isFullChangesAtCapacityAndAfterRemoval() {
        Inventory inventory = new Inventory(2);
        Item first = item("first");
        Item second = item("second");

        assertFalse(inventory.isFull());
        inventory.add(first);
        assertFalse(inventory.isFull());
        inventory.add(second);
        assertTrue(inventory.isFull());
        inventory.remove(first);
        assertFalse(inventory.isFull());
    }

    @Test
    void removeClearsOnlyTheMatchingSlotWithoutShiftingOtherItems() {
        Inventory inventory = new Inventory(3);
        Item first = item("first");
        Item middle = item("middle");
        Item last = item("last");
        inventory.add(first);
        inventory.add(middle);
        inventory.add(last);

        inventory.remove(middle);

        assertAll(
                () -> assertArrayEquals(new Item[] { first, null, last }, inventory.getItems()),
                () -> assertFalse(inventory.contains(middle)));
    }

    @Test
    void addReusesTheEarliestVacantSlot() {
        Inventory inventory = new Inventory(3);
        Item first = item("first");
        Item middle = item("middle");
        Item last = item("last");
        Item replacement = item("replacement");
        inventory.add(first);
        inventory.add(middle);
        inventory.add(last);
        inventory.remove(middle);

        inventory.add(replacement);

        assertArrayEquals(new Item[] { first, replacement, last }, inventory.getItems());
    }

    @Test
    void removingAnAbsentItemLeavesTheInventoryUntouched() {
        Inventory inventory = new Inventory(2);
        Item present = item("present");
        inventory.add(present);

        inventory.remove(item("absent"));

        assertArrayEquals(new Item[] { present, null }, inventory.getItems());
    }

    @Test
    void addingTheSameReferenceTwiceUsesTwoSlotsAndRemoveClearsOnlyFirstOccurrence() {
        Inventory inventory = new Inventory(2);
        Item duplicate = item("duplicate");
        inventory.add(duplicate);
        inventory.add(duplicate);

        assertArrayEquals(new Item[] { duplicate, duplicate }, inventory.getItems());
        inventory.remove(duplicate);

        assertAll(
                () -> assertArrayEquals(new Item[] { null, duplicate }, inventory.getItems()),
                () -> assertTrue(inventory.contains(duplicate)),
                () -> assertFalse(inventory.isFull()));
    }

    @Test
    void containsAndRemoveUseObjectIdentityEvenWhenEqualsReportsAMatch() {
        Inventory inventory = new Inventory(1);
        Item stored = item("same");
        Item equalButDistinct = new Item('!', Color.WHITE, "same", "same") {
            @Override
            public boolean equals(Object other) {
                return other instanceof Item;
            }

            @Override
            public int hashCode() {
                return 1;
            }
        };
        inventory.add(stored);

        assertFalse(inventory.contains(equalButDistinct));
        inventory.remove(equalButDistinct);

        assertSame(stored, inventory.get(0));
    }

    @Test
    void containsNullReflectsWhetherAnySlotIsEmpty() {
        Inventory inventory = new Inventory(1);
        Item item = item("item");

        assertTrue(inventory.contains(null));
        inventory.add(item);
        assertFalse(inventory.contains(null));
        inventory.remove(item);
        assertTrue(inventory.contains(null));
    }

    @Test
    void removeNullDoesNotChangeAnyOccupiedSlot() {
        Inventory inventory = new Inventory(2);
        Item item = item("item");
        inventory.add(item);

        inventory.remove(null);

        assertArrayEquals(new Item[] { item, null }, inventory.getItems());
    }

    @Test
    void getItemsExposesTheBackingArray() {
        Inventory inventory = new Inventory(1);
        Item item = item("item");

        inventory.getItems()[0] = item;

        assertAll(
                () -> assertSame(item, inventory.get(0)),
                () -> assertTrue(inventory.contains(item)),
                () -> assertTrue(inventory.isFull()));
    }
}
