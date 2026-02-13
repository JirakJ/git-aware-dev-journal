package com.jakubjirak.devjournal

import com.jakubjirak.devjournal.model.EntryType
import com.jakubjirak.devjournal.model.JournalEntry
import com.jakubjirak.devjournal.standup.StandupGenerator
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StandupGeneratorTest {

    @Test
    fun `should generate empty standup for no entries`() {
        val result = StandupGenerator.generateFromEntries(emptyList())
        assertTrue(result.contains("No activity"))
    }

    @Test
    fun `should include entry text in standup`() {
        val entries = listOf(
            createEntry("Implemented login flow", EntryType.NOTE),
            createEntry("Decided to use JWT", EntryType.DECISION),
        )
        val result = StandupGenerator.generateFromEntries(entries)
        assertTrue(result.contains("Implemented login flow"))
        assertTrue(result.contains("Decided to use JWT"))
    }

    @Test
    fun `should group entries by type`() {
        val entries = listOf(
            createEntry("Fix crash on startup", EntryType.NOTE),
            createEntry("Use Redis for caching", EntryType.DECISION),
            createEntry("Add retry logic", EntryType.TODO),
        )
        val result = StandupGenerator.generateFromEntries(entries)
        assertTrue(result.contains("Notes:"))
        assertTrue(result.contains("Decisions:"))
        assertTrue(result.contains("TODOs:"))
    }

    @Test
    fun `should include branch information`() {
        val entries = listOf(
            createEntry("Add search feature", EntryType.NOTE, branchName = "feature/search"),
        )
        val result = StandupGenerator.generateFromEntries(entries)
        assertTrue(result.contains("feature/search"))
    }

    @Test
    fun `should include commit hash context`() {
        val entries = listOf(
            createEntry("Refactored parser", EntryType.NOTE, commitHash = "abc1234def5678"),
        )
        val result = StandupGenerator.generateFromEntries(entries)
        assertTrue(result.contains("abc1234"))
    }

    @Test
    fun `should show branches worked on section`() {
        val entries = listOf(
            createEntry("Work on A", EntryType.NOTE, branchName = "feature/a"),
            createEntry("Work on B", EntryType.NOTE, branchName = "feature/b"),
        )
        val result = StandupGenerator.generateFromEntries(entries)
        assertTrue(result.contains("Branches worked on"))
        assertTrue(result.contains("feature/a"))
        assertTrue(result.contains("feature/b"))
    }

    @Test
    fun `should have standup summary header`() {
        val entries = listOf(createEntry("Some work", EntryType.NOTE))
        val result = StandupGenerator.generateFromEntries(entries)
        assertTrue(result.contains("Standup Summary"))
    }

    @Test
    fun `should not show branches section when no branches`() {
        val entries = listOf(createEntry("Work without branch", EntryType.NOTE))
        val result = StandupGenerator.generateFromEntries(entries)
        assertFalse(result.contains("Branches worked on"))
    }

    private fun createEntry(
        text: String,
        type: EntryType,
        branchName: String? = null,
        commitHash: String? = null,
    ): JournalEntry {
        return JournalEntry(
            id = "test-${System.nanoTime()}",
            text = text,
            entryType = type,
            branchName = branchName,
            commitHash = commitHash,
        )
    }
}
