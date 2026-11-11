package com.jakubjirak.devjournal

import com.jakubjirak.devjournal.model.EntryType
import com.jakubjirak.devjournal.model.JournalEntry
import com.jakubjirak.devjournal.narrative.BranchNarrativeBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BranchNarrativeBuilderTest {

    @Test
    fun `build keeps only the branch entries in chronological order`() {
        val entries = listOf(
            entry("b", "second", EntryType.NOTE, "feature/x", 2),
            entry("a", "first", EntryType.DECISION, "feature/x", 1),
            entry("c", "elsewhere", EntryType.NOTE, "main", 0),
            entry("d", "no branch", EntryType.NOTE, null, 0),
        )
        val narrative = BranchNarrativeBuilder.build("feature/x", entries)
        assertEquals("feature/x", narrative.branchName)
        assertEquals(listOf("a", "b"), narrative.entries.map { it.id })
        assertFalse(narrative.summary.contains("elsewhere"))
    }

    @Test
    fun `summary lists count and groups by type`() {
        val entries = listOf(
            entry("1", "Use Postgres", EntryType.DECISION, "f", 1),
            entry("2", "Flaky CI", EntryType.BLOCKER, "f", 2),
            entry("3", "Pick ORM", EntryType.DECISION, "f", 3),
        )
        val summary = BranchNarrativeBuilder.build("f", entries).summary
        assertTrue(summary.startsWith("Branch: f"))
        assertTrue(summary.contains("Entries: 3"))
        assertTrue(summary.contains("Decisions:\n  - Use Postgres\n  - Pick ORM"))
        assertTrue(summary.contains("Blockers:\n  - Flaky CI"))
    }

    @Test
    fun `empty branch yields explicit message`() {
        val narrative = BranchNarrativeBuilder.build("ghost", emptyList())
        assertTrue(narrative.entries.isEmpty())
        assertEquals("No activity recorded on branch 'ghost'.", narrative.summary)
    }

    private fun entry(id: String, text: String, type: EntryType, branch: String?, ts: Long) =
        JournalEntry(id = id, text = text, entryType = type, branchName = branch, timestamp = ts)
}
