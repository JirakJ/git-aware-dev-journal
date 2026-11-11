package com.jakubjirak.devjournal

import com.jakubjirak.devjournal.model.EntryType
import com.jakubjirak.devjournal.model.JournalEntry
import com.jakubjirak.devjournal.store.JournalRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

/**
 * Tests for [JournalRepository] — the persistence and query logic behind the project-level JournalStore.
 */
class JournalRepositoryTest {

    @TempDir
    lateinit var tempDir: Path

    private fun file() = File(tempDir.toFile(), ".dev-journal/entries.json")

    private fun repo(): JournalRepository = JournalRepository(file()).also { it.load() }

    @Test
    fun `missing file loads as empty journal without error`() {
        val repo = repo()
        assertEquals(0, repo.count())
        assertNull(repo.lastError)
        assertFalse(file().exists())
    }

    @Test
    fun `add persists entries and creates parent directory`() {
        val repo = repo()
        repo.add(entry("1", "First entry", EntryType.NOTE, "main"))
        repo.add(entry("2", "Second entry", EntryType.DECISION, "feature/auth"))

        assertTrue(file().isFile)
        val reloaded = repo()
        assertEquals(listOf("1", "2"), reloaded.all().map { it.id })
        assertEquals(EntryType.DECISION, reloaded.all()[1].entryType)
    }

    @Test
    fun `all fields survive a save and load round trip`() {
        val original = JournalEntry(
            id = "test-id-123",
            text = "Test entry with all fields",
            entryType = EntryType.BLOCKER,
            commitHash = "abc1234",
            branchName = "feature/test",
            filePath = "src/Main.kt",
            lineNumber = 42,
            timestamp = 1_700_000_000_000,
            author = "dev@test.com",
            tags = listOf("important", "blocker"),
        )
        repo().add(original)
        assertEquals(original, repo().all().single())
    }

    @Test
    fun `remove deletes by id and persists`() {
        val repo = repo()
        repo.add(entry("1", "a", EntryType.NOTE))
        repo.add(entry("2", "b", EntryType.NOTE))

        assertTrue(repo.remove("1"))
        assertFalse(repo.remove("does-not-exist"))
        assertEquals(listOf("2"), repo().all().map { it.id })
    }

    @Test
    fun `update replaces matching entry and ignores unknown id`() {
        val repo = repo()
        repo.add(entry("1", "old", EntryType.NOTE))

        assertTrue(repo.update(entry("1", "new", EntryType.TODO)))
        assertFalse(repo.update(entry("9", "ghost", EntryType.NOTE)))

        val reloaded = repo().all().single()
        assertEquals("new", reloaded.text)
        assertEquals(EntryType.TODO, reloaded.entryType)
    }

    @Test
    fun `filters by branch type and time`() {
        val repo = repo()
        repo.add(entry("1", "On main", EntryType.NOTE, "main").copy(timestamp = 1_000))
        repo.add(entry("2", "On feature", EntryType.NOTE, "feature/x").copy(timestamp = 2_000))
        repo.add(entry("3", "Also on main", EntryType.TODO, "main").copy(timestamp = 3_000))

        assertEquals(listOf("1", "3"), repo.byBranch("main").map { it.id })
        assertEquals(listOf("3"), repo.byType(EntryType.TODO).map { it.id })
        assertEquals(listOf("2", "3"), repo.since(2_000).map { it.id })
        assertTrue(repo.byBranch("nope").isEmpty())
    }

    @Test
    fun `search matches text and tags case-insensitively`() {
        val repo = repo()
        repo.add(entry("1", "Implemented OAuth login", EntryType.NOTE))
        repo.add(entry("2", "Fixed crash in parser", EntryType.NOTE, tags = listOf("Auth")))
        repo.add(entry("3", "Refactored UI", EntryType.DECISION, tags = listOf("ui")))

        assertEquals(listOf("1"), repo.search("LOGIN").map { it.id })
        assertEquals(listOf("1", "2"), repo.search("auth").map { it.id })
        assertEquals(3, repo.search("   ").size)
    }

    @Test
    fun `corrupt file reports error and starts empty`() {
        file().parentFile.mkdirs()
        file().writeText("{ this is not json")

        val repo = repo()
        assertEquals(0, repo.count())
        assertNotNull(repo.lastError)
        assertTrue(repo.lastError!!.contains("Corrupt"))
    }

    @Test
    fun `entries with missing required fields are dropped and missing tags defaulted`() {
        file().parentFile.mkdirs()
        file().writeText(
            """
            [
              {"id": "ok", "text": "kept", "entryType": "NOTE", "timestamp": 1},
              {"id": "no-text", "entryType": "NOTE", "timestamp": 2},
              {"text": "no id", "entryType": "NOTE", "timestamp": 3},
              {"id": "bad-type", "text": "x", "entryType": "NOT_A_TYPE", "timestamp": 4},
              null
            ]
            """.trimIndent(),
        )

        val all = repo().all()
        assertEquals(listOf("ok"), all.map { it.id })
        assertEquals(emptyList<String>(), all.single().tags)
        assertTrue(JournalRepository.matches(all.single(), "kept"))
    }

    @Test
    fun `literal null document loads as empty`() {
        file().parentFile.mkdirs()
        file().writeText("null")
        val repo = repo()
        assertEquals(0, repo.count())
        assertNull(repo.lastError)
    }

    @Test
    fun `save failure is reported via lastError`() {
        // A directory where the file should be makes the write fail.
        file().mkdirs()
        val repo = JournalRepository(file())
        repo.add(entry("1", "x", EntryType.NOTE))
        assertNotNull(repo.lastError)
        assertEquals(1, repo.count())
    }

    @Test
    fun `resolveStorageFile handles relative absolute and blank paths`() {
        val base = tempDir.toString()
        assertEquals(
            File(File(base, ".dev-journal"), "entries.json"),
            JournalRepository.resolveStorageFile(base, ".dev-journal"),
        )
        assertEquals(
            File(File(base, "docs/journal"), "entries.json"),
            JournalRepository.resolveStorageFile(base, " docs/journal "),
        )
        val absolute = File(tempDir.toFile(), "elsewhere").absolutePath
        assertEquals(File(absolute, "entries.json"), JournalRepository.resolveStorageFile(base, absolute))
        assertEquals(
            File(File(base, ".dev-journal"), "entries.json"),
            JournalRepository.resolveStorageFile(base, ""),
        )
        assertEquals(File(".dev-journal", "entries.json"), JournalRepository.resolveStorageFile(null, ".dev-journal"))
    }

    @Test
    fun `parseTags trims drops blanks and duplicates`() {
        assertEquals(listOf("auth", "ui"), JournalRepository.parseTags(" auth, ,ui,auth ,"))
        assertEquals(emptyList<String>(), JournalRepository.parseTags(null))
        assertEquals(emptyList<String>(), JournalRepository.parseTags("   "))
    }

    @Test
    fun `relativePath only strips base on a segment boundary`() {
        assertEquals("src/Main.kt", JournalRepository.relativePath("/work/proj", "/work/proj/src/Main.kt"))
        assertEquals("src/Main.kt", JournalRepository.relativePath("/work/proj/", "/work/proj/src/Main.kt"))
        assertEquals("/work/proj-other/A.kt", JournalRepository.relativePath("/work/proj", "/work/proj-other/A.kt"))
        assertEquals("/tmp/A.kt", JournalRepository.relativePath(null, "/tmp/A.kt"))
        assertEquals("src/A.kt", JournalRepository.relativePath("C:\\work\\proj", "C:\\work\\proj\\src\\A.kt"))
    }

    private fun entry(
        id: String,
        text: String,
        type: EntryType,
        branchName: String? = null,
        tags: List<String> = emptyList(),
    ) = JournalEntry(id = id, text = text, entryType = type, branchName = branchName, tags = tags, timestamp = 0)
}
