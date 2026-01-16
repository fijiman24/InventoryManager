package com.example.inventorymanager

import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyRightOf
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.inventorymanager.data.Inventory
import com.example.inventorymanager.data.InventoryItem
import com.example.inventorymanager.utils.FileStorage
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.containsString
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class DescExpInstrumentedTests {

    @Before
    fun setup() {
        // Clear inventory and storage before each test
        Inventory.items.clear()
        saveInventory()
    }

    private fun saveInventory() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        FileStorage(context).saveInventoryToFile(Inventory.items, "inventoryFile")
    }

    @Test
    fun incrementControls_arePositionedToRightOfItemName() {
        val item = InventoryItem("Apple", 10, "Fridge", "Desc", "2025-01-01")
        Inventory.items.add(item)
        saveInventory()

        ActivityScenario.launch(InventoryManager::class.java).use {
            // Check that the controls layout (or button) is to the right of the Name
            onView(withId(R.id.itemStockIncrementButton)).check(isCompletelyRightOf(withId(R.id.itemName)))
        }
    }

    @Test
    fun incrementControls_meetMinimumTouchTargetSize() {
        val item = InventoryItem("Apple", 10, "Fridge", "Desc", "2025-01-01")
        Inventory.items.add(item)
        saveInventory()

        ActivityScenario.launch(InventoryManager::class.java).use {
            // Check Width and Height >= 48dp
            onView(withId(R.id.itemStockIncrementButton)).check(
                matches(
                    hasMinimumDimensions(
                        48, 48
                    )
                )
            )

            onView(withId(R.id.itemStockDecrementButton)).check(
                matches(
                    hasMinimumDimensions(
                        48, 48
                    )
                )
            )
        }
    }

    @Test
    fun itemForm_displaysDescriptionAndExpirationDateFields() {
        val item = InventoryItem("TestItem", 5, "Fridge", "Test Desc", "2030-01-01")
        Inventory.items.add(item)
        saveInventory()

        ActivityScenario.launch(InventoryManager::class.java).use {
            // Check if Description and Date are displayed on the main list
            onView(withText("Test Desc")).check(matches(isDisplayed()))
            // We check for part of the date string
            onView(withText(containsString("2030-01-01"))).check(
                matches(
                    isDisplayed()
                )
            )
        }
    }

    @Test
    fun savingItem_allowsEmptyDescription() {
        ActivityScenario.launch(InventoryManager::class.java).use {
            // Navigate to Manual Entry Form
            onView(withId(R.id.fab)).perform(click())
            onView(withId(R.id.fab_manual_entry)).perform(click())

            // Fill required fields (Name & Stock) but leave Description and Expiration Date empty
            onView(withId(R.id.editItemName)).perform(typeText("Milk"), closeSoftKeyboard())
            onView(withId(R.id.editItemStock)).perform(typeText("1"), closeSoftKeyboard())

            // Click Save
            onView(withId(R.id.saveButton)).perform(click())

            // Assert that Description field has NO error (even though the form failed to save due to missing Date)
            onView(withId(R.id.editItemDescription)).check(matches(hasNoErrorText()))

            // Confirm that the Expiration Date DID get an error
            onView(withId(R.id.editItemExpirationDate)).check(matches(hasErrorText("This field is required")))
        }
    }

    @Test
    fun savingItem_requiresExpirationDate() {
        ActivityScenario.launch(InventoryManager::class.java).use {
            onView(withId(R.id.fab)).perform(click())
            onView(withId(R.id.fab_manual_entry)).perform(click())

            onView(withId(R.id.editItemName)).perform(typeText("Milk"), closeSoftKeyboard())
            onView(withId(R.id.editItemStock)).perform(typeText("1"), closeSoftKeyboard())

            // Leave Date Empty
            onView(withId(R.id.saveButton)).perform(click())

            // Verify we are still on the form (Save button still visible) or Error is shown
            onView(withId(R.id.saveButton)).check(matches(isDisplayed()))
            onView(withId(R.id.editItemExpirationDate)).check(matches(hasErrorText("This field is required")))
        }
    }

    @Test
    fun longDescription_isTruncatedWithEllipsis() {
        val longDesc = "This description is exactly thirty chars.." // 40 chars
        val expected = "This description is exact..." // 25 chars + ...

        val item = InventoryItem("TruncatedItem", 1, "Fridge", longDesc, "2025-01-01")
        Inventory.items.add(item)
        saveInventory()

        ActivityScenario.launch(InventoryManager::class.java).use {
            // Verify truncated text is displayed
            onView(withText(expected)).check(matches(isDisplayed()))
            // Verify full text is NOT displayed
            onView(withText(longDesc)).check(doesNotExist())
        }
    }

    @Test
    fun expirationDifference_isCalculatedAndRoundedCorrectly() {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // ~2.5 years (30 months) -> "2yr"
        val date2yr = today.plusMonths(30)
        // ~1.5 years (18 months) -> "1yr"
        val date1yr = today.plusMonths(18)
        // ~6 months -> "6mo"
        val date6mo = today.plusMonths(6)
        // ~45 days -> "1mo"
        val date1mo = today.plusDays(45)
        // 20 days -> "20d"
        val date20d = today.plusDays(20)
        // -20 days -> "-20d" (Expired)
        val dateMinus20d = today.minusDays(20)

        Inventory.items.addAll(
            listOf(
                InventoryItem("Item2yr", 1, "Fridge", "", date2yr.format(formatter)),
                InventoryItem("Item1yr", 1, "Fridge", "", date1yr.format(formatter)),
                InventoryItem("Item6mo", 1, "Fridge", "", date6mo.format(formatter)),
                InventoryItem("Item1mo", 1, "Fridge", "", date1mo.format(formatter)),
                InventoryItem("Item20d", 1, "Fridge", "", date20d.format(formatter)),
                InventoryItem("ItemExpired", 1, "Fridge", "", dateMinus20d.format(formatter))
            )
        )
        saveInventory()

        ActivityScenario.launch(InventoryManager::class.java).use {
            // Assert formatted text presence
            onView(withText(containsString("(2yr)"))).check(matches(isDisplayed()))
            onView(withText(containsString("(1yr)"))).check(matches(isDisplayed()))
            onView(withText(containsString("(6mo)"))).check(matches(isDisplayed()))
            onView(withText(containsString("(1mo)"))).check(matches(isDisplayed()))
            onView(withText(containsString("(20d)"))).check(matches(isDisplayed()))

            // Check Expired item: Text format AND Highlight (Red)
            onView(withText(containsString("(-20d)"))).check(matches(isDisplayed()))
                .check(matches(hasBackgroundColor(R.color.cadmiumRed)))
        }
    }

    @Test
    fun expirationDate_isHighlightedBasedOnStatus() {
        val today = LocalDate.now()

        // 1. Far Future (Normal)
        val futureDate = today.plusYears(2).toString()
        val itemNormal = InventoryItem("Normal", 1, "Fridge", "", futureDate)

        // 2. Expiring Soon (Yellow)
        val soonDate = today.plusDays(3).toString()
        val itemSoon = InventoryItem("Soon", 1, "Fridge", "", soonDate)

        // 3. Expired (Red)
        val expiredDate = today.minusDays(1).toString()
        val itemExpired = InventoryItem("Expired", 1, "Fridge", "", expiredDate)

        Inventory.items.addAll(listOf(itemNormal, itemSoon, itemExpired))
        saveInventory()

        ActivityScenario.launch(InventoryManager::class.java).use {
            // Check Normal (No Background or White/Transparent)
            onView(withText(containsString(futureDate))).check(
                matches(
                    hasBackgroundColor(null)
                )
            ) // Assuming null/transparent

            // Check Warning (Yellow)
            onView(withText(containsString(soonDate))).check(
                matches(
                    hasBackgroundColor(R.color.warningYellow)
                )
            )

            // Check Expired (Red)
            onView(withText(containsString(expiredDate))).check(
                matches(
                    hasBackgroundColor(R.color.cadmiumRed)
                )
            )
        }
    }

    // --- Custom Matchers ---
    private fun hasMinimumDimensions(minWidthDp: Int, minHeightDp: Int): Matcher<View> {
        return object : BoundedMatcher<View, View>(View::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has width >= $minWidthDp dp and height >= $minHeightDp dp")
            }

            override fun matchesSafely(view: View): Boolean {
                val density = view.resources.displayMetrics.density
                val widthDp = view.width / density
                val heightDp = view.height / density
                return widthDp >= minWidthDp && heightDp >= minHeightDp
            }
        }
    }

    private fun hasBackgroundColor(colorResId: Int?): Matcher<View> {
        return object : BoundedMatcher<View, View>(View::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has background color resource ID: $colorResId")
            }

            override fun matchesSafely(view: View): Boolean {
                val context = view.context
                val expectedColor =
                    if (colorResId != null) ContextCompat.getColor(context, colorResId) else null

                if (expectedColor == null) {
                    return view.background == null
                }

                if (view.background is android.graphics.drawable.ColorDrawable) {
                    return (view.background as android.graphics.drawable.ColorDrawable).color == expectedColor
                }
                return false
            }
        }
    }

    private fun hasNoErrorText(): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("has no error text")
            }

            override fun matchesSafely(view: View): Boolean {
                return if (view is EditText) {
                    view.error == null
                } else {
                    false
                }
            }
        }
    }
}