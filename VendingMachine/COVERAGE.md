For the test cases I wrote, my initial coverage was 100% in both VendingMachine.java and VendingMachineItem.java (as well as VendingMachineException.java).

As a result of this, I didn't add any new tests since I was already at 100%

For the condition in makePurchase(), my test suite sufficiently tests everything since I have tests checking for invalid codes, null codes, empty slots, an insufficient balance, as well as a test to make sure that everything functions properly. The main two boolean conditions here are that the item isnt null/invalid, and that there is a sufficient balance, which all of my tests in combination thoroughly check.

My injected fault caused a significant number of tests to fail, for the fault, i changed the condition in insertMoney() to be amount > 0, instead of amount < 0. The tests that failed as a result of this change were:
- testInsertMoneyPositive()
- testInsertMoneyMultiple()
- testInsertMoneyNegative()
- testGetBalanceAfterInsertion()
- testMakePurchase()
- testMakePurchaseInsufficient()
- testMakePurchaseExact()
- testMakePurchaseEmpty
- testReturnChange()
The reason all of these tests failed was since they needed to insert money at some point, and the new condition made that basically impossible to do without an exception being thrown.