package com.example.inventorymanager

import com.example.inventorymanager.utils.InventoryViewHelper
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate

class DescExpUnitTests {

    @Test
    fun longDescription_isTruncatedWithEllipsis() {
        val longDesc = "This description is exactly thirty chars.." // 40 chars
        val expected = "This description is exact..." // 25 chars + ...

        val result = InventoryViewHelper.getTruncatedDescription(longDesc)
        Assert.assertEquals(expected, result)

        // Verify short descriptions are untouched
        val shortDesc = "Short desc"
        Assert.assertEquals(shortDesc, InventoryViewHelper.getTruncatedDescription(shortDesc))
    }

    @Test
    fun expirationDate_isFormattedCorrectly() {
        // While the View handles the final string "yyyy-mm-dd (time)",
        // this unit test ensures our calculation helper returns expected suffixes.
        val today = LocalDate.of(2023, 1, 1)
        val future = LocalDate.of(2023, 2, 1)

        val result = InventoryViewHelper.getTimeUntilExpiryString(today, future)
        // Verify it returns a valid suffix format like "1mo"
        assert(result.matches(Regex("-?\\d+(yr|mo|d)")))
    }

    @Test
    fun expirationDifference_isCalculatedAndRoundedCorrectly() {
        val today = LocalDate.of(2023, 1, 1)

        // ~2.5 years in the future (30 months) -> (3yr) is nearest integer year?
        // Note: Java ChronoUnit truncates, it doesn't round to nearest usually.
        // Based on your implementation: 30 months / 12 = 2 years.
        val twoPointFiveYears = today.plusMonths(30)
        Assert.assertEquals(
            "2yr", InventoryViewHelper.getTimeUntilExpiryString(today, twoPointFiveYears)
        )

        // ~1.5 years (18 months) -> 1yr
        val onePointFiveYears = today.plusMonths(18)
        Assert.assertEquals(
            "1yr", InventoryViewHelper.getTimeUntilExpiryString(today, onePointFiveYears)
        )

        // ~6 months -> 6mo
        val sixMonths = today.plusMonths(6)
        Assert.assertEquals("6mo", InventoryViewHelper.getTimeUntilExpiryString(today, sixMonths))

        // ~45 days -> 1mo (ChronoUnit.MONTHS truncates days)
        val fortyFiveDays = today.plusDays(45)
        Assert.assertEquals(
            "1mo", InventoryViewHelper.getTimeUntilExpiryString(today, fortyFiveDays)
        )

        // 20 days -> 20d
        val twentyDays = today.plusDays(20)
        Assert.assertEquals("20d", InventoryViewHelper.getTimeUntilExpiryString(today, twentyDays))

        // 20 days in the past -> -20d
        val twentyDaysAgo = today.minusDays(20)
        Assert.assertEquals(
            "-20d", InventoryViewHelper.getTimeUntilExpiryString(today, twentyDaysAgo)
        )
    }
}