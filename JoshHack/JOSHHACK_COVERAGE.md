# Coverage Rundown

| Coverage | Baseline | Final |
| -------- | -------- | ----- |
| Statement | 0% | 100% |
| Function | 0% | 100% |
| Branches | 0% | 100%|

# Tests
| Test Case | Description |
| --------- | ----------- |
| newInventoryHasRequestedCapacityAndEmptySlots() | Creates an inventory with three slots, reads its item array, and confirms all three slots start empty |
| zeroCapacityInventoryIsFullAndCannotStoreAnItem() | Creates an inventory with no slots, tries to add a potion, and observes that it remains full and contains no potion |
| negativeCapacityIsRejected() | Attempts to create an inventory with capacity −1 and expects construction to throw a NegativeArraySizeException |
| getReturnsTheItemAtEachOccupidedIndexAndNullAtEmptyIndexes() | Adds two items to a three-slot inventory, then reads all three indexes to confirm their positions and the remaining empty slot |
| getRejectsIndexesOutsideTheInventory() | Reads index −1 and index 2 from a two-slot inventory and expects an exception for each |
| addFillsTheFirstAvailableSlot() | Adds two different items to a three-slot inventory and compares the resulting slot order with [first, second, empty] |
| addingToAFullInventoryLeavesExistingItemsUntouched() | Fills both slots, attempts to add a third item, and confirms the original two remain in place |
| addingNullLeavesAnEmptySlotAvailable() | Passes null to a one-slot inventory and confirms the slot remains available. |
| isFullChangesAtCapacityAndAfterRemoval() | Starts with two empty slots, adds items one at a time, then removes one, observing the full status after each step |
| removeClearsOnlyTheMatchingSlotWithoutShiftingOtherItems() | Fills three slots, removes the middle item, and confirms the first and last items stay at their original indexes |
| addReusesTheEarliestVacantSlot() | Fills three slots, removes the middle item, then adds a replacement and confirms it occupies that gap |
| removingAnAbsentItemLeavesTheInventoryUntouched() | Stores one item, tries to remove a different item, and confirms the stored item remains |
| addingTheSameReferenceTwiceUsesTwoSlotsAndRemoveClearsOnlyFirstOccurrence() | Adds the same object twice, removes it once, and confirms only its first occurrence is cleared |
| containsAndRemoveUseObjectIdentityEvenWhenEqualsReportsAMatch() | Stores an item, creates a separate item whose equals method reports a match, then confirms that lookup and removal do not treat it as the stored object|
| containsNullReflectsWhetherAnySlotIsEmpty() | Queries contains(null) while the only slot is empty, occupied, and empty again after removal |
| removeNullDoesNotChangeAnyOccupiedSlot() | Stores an item alongside an empty slot, calls remove(null), and confirms the stored item remains |
| getItemsExposesTheBackingArray() | Writes an item directly into the array returned by getItems(), then reads it through the inventory’s other methods |