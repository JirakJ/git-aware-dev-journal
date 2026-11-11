package com.jakubjirak.devjournal

import com.jakubjirak.devjournal.model.EntryType
import com.jakubjirak.devjournal.model.JournalEntry
import com.jakubjirak.devjournal.standup.StandupGenerator
import org.junit.jupiter.api.Assertions.assertEquals
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

    @Test
    fun `generate keeps only entries inside the time window newest first`() {
        val now = 100 * HOUR
        val entries = listOf(
            createEntry("too old", EntryType.NOTE).copy(timestamp = now - 25 * HOUR),
            createEntry("older", EntryType.NOTE).copy(timestamp = now - 20 * HOUR),
            createEntry("newest", EntryType.NOTE).copy(timestamp = now - 1 * HOUR),
        )
        val result = StandupGenerator.generate(entries, timeWindowHours = 24, nowMillis = now)
        assertFalse(result.contains("too old"))
        assertTrue(result.indexOf("newest") in 0 until result.indexOf("older"))
    }

    @Test
    fun `generate reports the window when nothing is recent`() {
        val now = 100 * HOUR
        val entries = listOf(createEntry("ancient", EntryType.NOTE).copy(timestamp = 0))
        val result = StandupGenerator.generate(entries, timeWindowHours = 8, nowMillis = now)
        assertEquals("No activity recorded in the last 8 hours.", result)
    }

    @Test
    fun `generate clamps a non-positive window to one hour`() {
        val now = 100 * HOUR
        val entries = listOf(createEntry("recent", EntryType.NOTE).copy(timestamp = now - HOUR / 2))
        assertTrue(StandupGenerator.generate(entries, timeWindowHours = 0, nowMillis = now).contains("recent"))
    }

    @Test
    fun `generateFromEntries on empty list has no misleading window`() {
        assertEquals("No activity recorded.", StandupGenerator.generateFromEntries(emptyList()))
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

    private companion object {
        const val HOUR = 3_600_000L
    }
}
