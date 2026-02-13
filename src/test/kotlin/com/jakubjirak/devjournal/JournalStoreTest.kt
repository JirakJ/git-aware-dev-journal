package com.jakubjirak.devjournal

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.jakubjirak.devjournal.model.EntryType
import com.jakubjirak.devjournal.model.JournalEntry
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import java.util.UUID

/**
 * Tests for journal entry serialization and filtering logic.
 * Does not depend on IntelliJ platform services.
 */
class JournalStoreTest {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    @Test
    fun `should serialize and deserialize entries`(@TempDir tempDir: Path) {
        val entries = listOf(
            createEntry("First entry", EntryType.NOTE, "main"),
            createEntry("Second entry", EntryType.DECISION, "feature/auth"),
        )

        val file = File(tempDir.toFile(), "entries.json")
        file.writeText(gson.toJson(entries))

        val type = object : TypeToken<List<JournalEntry>>() {}.type
        val loaded: List<JournalEntry> = gson.fromJson(file.readText(), type)

        assertEquals(2, loaded.size)
        assertEquals("First entry", loaded[0].text)
        assertEquals(EntryType.DECISION, loaded[1].entryType)
    }

    @Test
    fun `should filter entries by branch`() {
        val entries = listOf(
            createEntry("On main", EntryType.NOTE, "main"),
            createEntry("On feature", EntryType.NOTE, "feature/x"),
            createEntry("Also on main", EntryType.TODO, "main"),
        )

        val filtered = entries.filter { it.branchName == "main" }
        assertEquals(2, filtered.size)
    }

    @Test
    fun `should filter entries by type`() {
        val entries = listOf(
            createEntry("Note entry", EntryType.NOTE),
            createEntry("Decision entry", EntryType.DECISION),
            createEntry("Another note", EntryType.NOTE),
        )

        val filtered = entries.filter { it.entryType == EntryType.NOTE }
        assertEquals(2, filtered.size)
    }

    @Test
    fun `should filter entries since timestamp`() {
        val now = System.currentTimeMillis()
        val entries = listOf(
            createEntry("Old entry", EntryType.NOTE).copy(timestamp = now - 100_000),
            createEntry("Recent entry", EntryType.NOTE).copy(timestamp = now - 1_000),
            createEntry("Very old", EntryType.NOTE).copy(timestamp = now - 500_000),
        )

        val cutoff = now - 50_000
        val filtered = entries.filter { it.timestamp >= cutoff }
        assertEquals(1, filtered.size)
        assertEquals("Recent entry", filtered[0].text)
    }

    @Test
    fun `should search entries by text`() {
        val entries = listOf(
            createEntry("Implemented OAuth login", EntryType.NOTE),
            createEntry("Fixed crash in parser", EntryType.NOTE),
            createEntry("Refactored login flow", EntryType.DECISION),
        )

        val query = "login"
        val filtered = entries.filter { it.text.lowercase().contains(query.lowercase()) }
        assertEquals(2, filtered.size)
    }

    @Test
    fun `should search entries by tags`() {
        val entries = listOf(
            createEntry("Entry with auth tag", EntryType.NOTE, tags = listOf("auth", "security")),
            createEntry("Entry with ui tag", EntryType.NOTE, tags = listOf("ui")),
            createEntry("Entry with auth tag too", EntryType.NOTE, tags = listOf("auth")),
        )

        val query = "auth"
        val filtered = entries.filter { entry ->
            entry.tags.any { it.lowercase().contains(query.lowercase()) }
        }
        assertEquals(2, filtered.size)
    }

    @Test
    fun `should handle empty entries list`(@TempDir tempDir: Path) {
        val file = File(tempDir.toFile(), "entries.json")
        file.writeText(gson.toJson(emptyList<JournalEntry>()))

        val type = object : TypeToken<List<JournalEntry>>() {}.type
        val loaded: List<JournalEntry> = gson.fromJson(file.readText(), type)
        assertTrue(loaded.isEmpty())
    }

    @Test
    fun `should preserve all entry fields through serialization`(@TempDir tempDir: Path) {
        val entry = JournalEntry(
            id = "test-id-123",
            text = "Test entry with all fields",
            entryType = EntryType.BLOCKER,
            commitHash = "abc1234",
            branchName = "feature/test",
            filePath = "src/Main.kt",
            lineNumber = 42,
            timestamp = 1700000000000,
            author = "dev@test.com",
            tags = listOf("important", "blocker"),
        )

        val file = File(tempDir.toFile(), "entries.json")
        file.writeText(gson.toJson(listOf(entry)))

        val type = object : TypeToken<List<JournalEntry>>() {}.type
        val loaded: List<JournalEntry> = gson.fromJson(file.readText(), type)

        assertEquals(1, loaded.size)
        val restored = loaded[0]
        assertEquals("test-id-123", restored.id)
        assertEquals("Test entry with all fields", restored.text)
        assertEquals(EntryType.BLOCKER, restored.entryType)
        assertEquals("abc1234", restored.commitHash)
        assertEquals("feature/test", restored.branchName)
        assertEquals("src/Main.kt", restored.filePath)
        assertEquals(42, restored.lineNumber)
        assertEquals(1700000000000, restored.timestamp)
        assertEquals("dev@test.com", restored.author)
        assertEquals(listOf("important", "blocker"), restored.tags)
    }

    private fun createEntry(
        text: String,
        type: EntryType,
        branchName: String? = null,
        tags: List<String> = emptyList(),
    ): JournalEntry {
        return JournalEntry(
            id = UUID.randomUUID().toString(),
            text = text,
            entryType = type,
            branchName = branchName,
            tags = tags,
        )
    }
}
