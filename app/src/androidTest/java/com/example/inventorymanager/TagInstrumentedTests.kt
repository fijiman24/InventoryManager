package com.example.inventorymanager

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventorymanager.data.Inventory
import com.example.inventorymanager.data.InventoryTags
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TagInstrumentedTests {

    @get:Rule
    val activityRule = ActivityScenarioRule(InventoryManager::class.java)

    @Before
    fun setUp() {
        Inventory.items.clear()
        InventoryTags.tags.clear()
    }

    @After
    fun tearDown() {
        Inventory.items.clear()
        InventoryTags.tags.clear()
    }

    @Test
    fun createItem_CanBeCreatedWithoutTags() {
        // 1. Navigate to Add Item
        onView(withId(R.id.fab)).perform(click())

        // 2. Fill basic info only (Name, Stock, Date, Location)
        onView(withId(R.id.editItemName)).perform(typeText("Apple"), closeSoftKeyboard())
        onView(withId(R.id.editItemStock)).perform(typeText("10"), closeSoftKeyboard())

        // Handle Date Picker (Click input -> Click OK on dialog)
        onView(withId(R.id.editItemExpirationDate)).perform(click())
        onView(withText("OK")).perform(click())

        // Handle Location Selector
        onView(withId(R.id.itemLocationSelector)).perform(click())
        onView(withId(R.id.searchBar)).perform(typeText("Fridge"))
        onView(withId(R.id.itemName)).perform(click())

        // 3. Skip the Tag Selector and Click Save
        onView(withId(R.id.saveButton)).perform(click())

        // 4. Verify the item name appears on the Main Screen
        onView(withText("Apple")).check(matches(isDisplayed()))
    }

    @Test
    fun tagInput_EnforcesCharacterLimit() {
        // 1. Open Item Form
        onView(withId(R.id.fab)).perform(click())

        // 2. Open Tag Selector
        onView(withId(R.id.itemTagSelector)).perform(click())

        // 3. Attempt to type a 25-character tag
        val longTag = "1234567890123456789012345" // 25 chars
        onView(withId(R.id.searchBar)).perform(typeText(longTag))

        // 4. Verify text in search bar is truncated to 20 chars
        val expectedTruncated = "12345678901234567890"
        onView(withId(R.id.searchBar)).check(matches(withText(expectedTruncated)))
    }

    @Test
    fun locationInput_DoesNotEnforceTagLimit() {
        // Regression Check: Ensure we didn't accidentally limit locations too
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.itemLocationSelector)).perform(click())

        val longLocation = "Shelf A - Compartment 45 - Bin 2" // > 20 chars
        onView(withId(R.id.searchBar)).perform(typeText(longLocation))

        // Should accept the full string
        onView(withId(R.id.searchBar)).check(matches(withText(longLocation)))
    }

    @Test
    fun createItemWithTags_DisplaysCorrectlyOnMainPage() {
        val tagName1 = "Fruit"
        val tagName2 = "Chilled"
        val itemName = "Apple"

        // 1. Navigate to Add Item
        onView(withId(R.id.fab)).perform(click())

        // 2. Fill basic info
        onView(withId(R.id.editItemName)).perform(typeText(itemName), closeSoftKeyboard())
        onView(withId(R.id.editItemStock)).perform(typeText("10"), closeSoftKeyboard())
        onView(withId(R.id.editItemExpirationDate)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.itemLocationSelector)).perform(click())
        onView(withId(R.id.searchBar)).perform(typeText("Fridge"))
        onView(withId(R.id.itemName)).perform(click())

        // 3. Add Tags
        onView(withId(R.id.itemTagSelector)).perform(click())
        onView(withId(R.id.searchBar)).perform(typeText(tagName1))
        onView(withId(R.id.itemName)).perform(click())

        onView(withId(R.id.itemTagSelector)).perform(click())
        onView(withId(R.id.searchBar)).perform(typeText(tagName2))
        onView(withId(R.id.itemName)).perform(click())

        // 4. Save
        onView(withId(R.id.saveButton)).perform(click())

        // 5. Verify Tags appear on Main Screen
        onView(withText(tagName1)).check(matches(isDisplayed()))
        onView(withText(tagName2)).check(matches(isDisplayed()))
    }

    @Test
    fun createItemWithTags_ShowsEllipsisForMoreThanThreeTags() {
        // 1. Create item with 4 tags
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.editItemName)).perform(typeText("ManyTagsItem"), closeSoftKeyboard())
        onView(withId(R.id.editItemStock)).perform(typeText("1"), closeSoftKeyboard())
        onView(withId(R.id.editItemExpirationDate)).perform(click())
        onView(withText("OK")).perform(click())

        onView(withId(R.id.itemLocationSelector)).perform(click())
        onView(withId(R.id.searchBar)).perform(typeText("Shelf"))
        onView(withId(R.id.itemName)).perform(click())

        val tags = listOf("Tag1", "Tag2", "Tag3", "Tag4")
        tags.forEach { tag ->
            onView(withId(R.id.itemTagSelector)).perform(click())
            onView(withId(R.id.searchBar)).perform(typeText(tag), closeSoftKeyboard())
            Thread.sleep(1000)
            onView(withId(R.id.itemName)).perform(click())
        }

        onView(withId(R.id.saveButton)).perform(click())

        // 2. Verify: First 3 visible, 4th hidden, Ellipsis visible
        onView(withText("Tag1")).check(matches(isDisplayed()))
        onView(withText("Tag2")).check(matches(isDisplayed()))
        onView(withText("Tag3")).check(matches(isDisplayed()))

        onView(withText("Tag4")).check(doesNotExist())
        onView(withText("...")).check(matches(isDisplayed()))
    }

    @Test
    fun removeTag_UsedTagShowsWarningModalAndRemovesTag() {
        // 1. Create an item that uses the tag "Important"
        createItemWithTag("Important")

        // 2. Click the new item to edit it
        onView(withText("Dummy")).perform(click())

        // 3. Open Tag Selector
        onView(withId(R.id.itemTagSelector)).perform(click())

        // 4. Try to delete "Important"
        onView(allOf(withId(R.id.actionIcon), hasSibling(withText("Important")))).perform(click())

        // 5. Verify Warning Modal and Click Yes
        onView(withText("1 items currently have this tag. If you delete this tag, those items will lose that tag. Are you sure you want to delete this tag?")).inRoot(
            isDialog()
        ).check(matches(isDisplayed()))

        onView(withText("Yes")).inRoot(isDialog()).perform(click())

        // 6. Verify Tag is gone from Selector
        onView(withText("Important")).check(doesNotExist())

        // 7. Close Selector and Click Save
        pressBack() // Closes the selector dialog
        onView(withId(R.id.saveButton)).perform(click())

        // 8. Verify the item exists but the "Important" tag is gone
        onView(withText("Dummy")).check(matches(isDisplayed()))
        onView(withText("Important")).check(doesNotExist())
    }

    @Test
    fun removeTag_UnusedTagDeletesInstantly() {
        // 1. Open Item Form to create the tag
        onView(withId(R.id.fab)).perform(click())

        // 2. Create/Add a tag "Unused"
        onView(withId(R.id.itemTagSelector)).perform(click())
        onView(withId(R.id.searchBar)).perform(typeText("Unused"))
        onView(withId(R.id.itemName)).perform(click())
        Thread.sleep(1000)

        // 3. Exit the form WITHOUT saving (Press Back)
        pressBack()

        // 4. Open Item Form again (Fresh state, no tags selected)
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.itemTagSelector)).perform(click())

        // 5. Delete "Unused" from the selector
        onView(allOf(withId(R.id.actionIcon), hasSibling(withText("Unused")))).perform(click())

        // 6. Verify NO Modal (Dialog should not exist)
        onView(withText("Delete Tag?")).check(doesNotExist())
        onView(withText("Unused")).check(doesNotExist())
    }

    // --- Helper ---
    private fun createItemWithTag(tag: String) {
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.editItemName)).perform(typeText("Dummy"), closeSoftKeyboard())
        onView(withId(R.id.editItemStock)).perform(typeText("1"), closeSoftKeyboard())
        onView(withId(R.id.editItemExpirationDate)).perform(click())
        onView(withText("OK")).perform(click())

        onView(withId(R.id.itemLocationSelector)).perform(click())
        onView(withId(R.id.searchBar)).perform(typeText("Loc"))
        onView(withId(R.id.itemName)).perform(click())

        onView(withId(R.id.itemTagSelector)).perform(click())
        onView(withId(R.id.searchBar)).perform(typeText(tag))
        onView(withId(R.id.itemName)).perform(click())

        onView(withId(R.id.saveButton)).perform(click())
    }
}