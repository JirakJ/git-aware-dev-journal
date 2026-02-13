package com.jakubjirak.devjournal.licensing

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service

/**
 * Central gate for freemium feature access.
 * Free tier: 50 entries max, basic journal and branch narrative.
 * Paid tier: unlimited entries, sync, team features, standup generator.
 */
@Service(Service.Level.APP)
class FreemiumGate {

    enum class LicenseStatus {
        FREE, TRIAL, PAID, EXPIRED
    }

    @Suppress("UNUSED_PARAMETER")
    fun isPaidFeatureAvailable(featureId: String): Boolean {
        val status = getLicenseStatus()
        return status == LicenseStatus.PAID || status == LicenseStatus.TRIAL
    }

    fun getLicenseStatus(): LicenseStatus {
        // TODO: Integrate with JetBrains Marketplace Licensing API
        return LicenseStatus.FREE
    }

    fun canAddEntry(currentCount: Int): Boolean {
        return when (getLicenseStatus()) {
            LicenseStatus.FREE, LicenseStatus.EXPIRED -> currentCount < MAX_FREE_ENTRIES
            LicenseStatus.TRIAL, LicenseStatus.PAID -> true
        }
    }

    fun getMaxEntries(): Int {
        return when (getLicenseStatus()) {
            LicenseStatus.FREE, LicenseStatus.EXPIRED -> MAX_FREE_ENTRIES
            LicenseStatus.TRIAL, LicenseStatus.PAID -> Int.MAX_VALUE
        }
    }

    companion object {
        const val MAX_FREE_ENTRIES = 50

        // Feature IDs
        const val FEATURE_SYNC = "sync"
        const val FEATURE_TEAM = "team"
        const val FEATURE_STANDUP = "standup"
        const val FEATURE_UNLIMITED_ENTRIES = "unlimited_entries"

        fun getInstance(): FreemiumGate =
            ApplicationManager.getApplication().getService(FreemiumGate::class.java)
    }
}
