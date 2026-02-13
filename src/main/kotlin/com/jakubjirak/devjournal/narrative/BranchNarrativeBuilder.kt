package com.jakubjirak.devjournal.narrative

import com.jakubjirak.devjournal.model.BranchNarrative
import com.jakubjirak.devjournal.model.JournalEntry

/**
 * Builds a narrative summary from journal entries associated with a branch.
 */
object BranchNarrativeBuilder {

    fun build(branchName: String, entries: List<JournalEntry>): BranchNarrative {
        val branchEntries = entries
            .filter { it.branchName == branchName }
            .sortedBy { it.timestamp }

        val summary = generateSummary(branchName, branchEntries)

        return BranchNarrative(
            branchName = branchName,
            entries = branchEntries,
            summary = summary,
        )
    }

    internal fun generateSummary(branchName: String, entries: List<JournalEntry>): String {
        if (entries.isEmpty()) {
            return "No activity recorded on branch '$branchName'."
        }

        val sb = StringBuilder()
        sb.appendLine("Branch: $branchName")
        sb.appendLine("Entries: ${entries.size}")
        sb.appendLine()

        val byType = entries.groupBy { it.entryType }
        for ((type, typeEntries) in byType) {
            sb.appendLine("${type.label}s:")
            typeEntries.forEach { entry ->
                sb.appendLine("  - ${entry.text}")
            }
            sb.appendLine()
        }

        return sb.toString().trim()
    }
}
