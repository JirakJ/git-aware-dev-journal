package com.jakubjirak.devjournal.standup

import com.jakubjirak.devjournal.model.JournalEntry
import com.jakubjirak.devjournal.settings.DevJournalSettings

/**
 * Generates standup summaries from recent journal entries and commit activity.
 */
object StandupGenerator {

    fun generate(allEntries: List<JournalEntry>, timeWindowHours: Int? = null): String {
        val hours = timeWindowHours ?: DevJournalSettings.getInstance().standupTimeWindow
        val cutoff = System.currentTimeMillis() - (hours * MILLIS_PER_HOUR)

        val recentEntries = allEntries
            .filter { it.timestamp >= cutoff }
            .sortedByDescending { it.timestamp }

        return formatStandup(recentEntries, hours)
    }

    fun generateFromEntries(entries: List<JournalEntry>): String {
        return formatStandup(entries, 0)
    }

    internal fun formatStandup(entries: List<JournalEntry>, hours: Int): String {
        if (entries.isEmpty()) {
            return "No activity recorded in the last $hours hours."
        }

        val sb = StringBuilder()
        sb.appendLine("## Standup Summary")
        sb.appendLine()
        sb.appendLine("### What I did:")

        val byType = entries.groupBy { it.entryType }
        for ((type, typeEntries) in byType) {
            sb.appendLine()
            sb.appendLine("**${type.label}s:**")
            typeEntries.forEach { entry ->
                val context = buildContextString(entry)
                sb.appendLine("- ${entry.text}$context")
            }
        }

        val branches = entries.mapNotNull { it.branchName }.distinct()
        if (branches.isNotEmpty()) {
            sb.appendLine()
            sb.appendLine("### Branches worked on:")
            branches.forEach { sb.appendLine("- $it") }
        }

        return sb.toString().trim()
    }

    private fun buildContextString(entry: JournalEntry): String {
        val parts = mutableListOf<String>()
        entry.branchName?.let { parts.add(it) }
        entry.commitHash?.let { parts.add(it.take(SHORT_HASH_LENGTH)) }
        return if (parts.isNotEmpty()) " (${parts.joinToString(", ")})" else ""
    }

    private const val MILLIS_PER_HOUR = 3_600_000L
    private const val SHORT_HASH_LENGTH = 7
}
