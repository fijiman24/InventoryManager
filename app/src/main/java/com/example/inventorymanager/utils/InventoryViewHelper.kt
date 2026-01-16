package com.example.inventorymanager.utils

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

object InventoryViewHelper {

    /**
     * Truncates description to 25 characters followed by an ellipsis if longer.
     */
    fun getTruncatedDescription(description: String): String {
        return if (description.length > 25) "${description.take(25)}..." else description
    }

    /**
     * Calculates the time difference string (Year, Month, or Day).
     */
    fun getTimeUntilExpiryString(today: LocalDate, expiryDate: LocalDate): String {
        val daysUntil = ChronoUnit.DAYS.between(today, expiryDate)
        val monthsUntil = ChronoUnit.MONTHS.between(today, expiryDate)
        val yearsUntil = ChronoUnit.YEARS.between(today, expiryDate)

        return when {
            abs(yearsUntil) >= 1 -> "${yearsUntil}yr"
            abs(monthsUntil) >= 1 -> "${monthsUntil}mo"
            else -> "${daysUntil}d"
        }
    }

    /**
     * Determines the status based on days remaining.
     */
    fun getExpirationStatus(today: LocalDate, expiryDate: LocalDate): ExpirationStatus {
        val daysUntil = ChronoUnit.DAYS.between(today, expiryDate)
        return when {
            daysUntil < 0 -> ExpirationStatus.EXPIRED
            daysUntil < 7 -> ExpirationStatus.WARNING
            else -> ExpirationStatus.NORMAL
        }
    }

    enum class ExpirationStatus {
        NORMAL, WARNING, EXPIRED
    }
}