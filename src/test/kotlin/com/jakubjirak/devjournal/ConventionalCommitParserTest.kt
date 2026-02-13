package com.jakubjirak.devjournal

import com.jakubjirak.devjournal.git.ConventionalCommitParser
import com.jakubjirak.devjournal.model.EntryType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConventionalCommitParserTest {

    @Test
    fun `should detect conventional commit`() {
        assertTrue(ConventionalCommitParser.isConventionalCommit("feat: add new feature"))
        assertTrue(ConventionalCommitParser.isConventionalCommit("fix: resolve bug"))
        assertTrue(ConventionalCommitParser.isConventionalCommit("docs: update readme"))
        assertTrue(ConventionalCommitParser.isConventionalCommit("refactor: clean up code"))
        assertTrue(ConventionalCommitParser.isConventionalCommit("chore: update deps"))
    }

    @Test
    fun `should reject non-conventional commit`() {
        assertFalse(ConventionalCommitParser.isConventionalCommit("Update readme"))
        assertFalse(ConventionalCommitParser.isConventionalCommit("WIP"))
        assertFalse(ConventionalCommitParser.isConventionalCommit(""))
        assertFalse(ConventionalCommitParser.isConventionalCommit("merge branch main"))
    }

    @Test
    fun `should parse feat commit`() {
        val entry = ConventionalCommitParser.parse("feat: add user authentication")
        assertNotNull(entry)
        assertEquals("add user authentication", entry!!.text)
        assertEquals(EntryType.INSIGHT, entry.entryType)
        assertTrue(entry.tags.contains("feat"))
    }

    @Test
    fun `should parse fix commit`() {
        val entry = ConventionalCommitParser.parse("fix: resolve null pointer exception")
        assertNotNull(entry)
        assertEquals("resolve null pointer exception", entry!!.text)
        assertEquals(EntryType.NOTE, entry.entryType)
        assertTrue(entry.tags.contains("fix"))
    }

    @Test
    fun `should parse commit with scope`() {
        val entry = ConventionalCommitParser.parse("feat(auth): add OAuth2 support")
        assertNotNull(entry)
        assertEquals("add OAuth2 support", entry!!.text)
        assertTrue(entry.tags.contains("feat"))
        assertTrue(entry.tags.contains("auth"))
    }

    @Test
    fun `should parse refactor commit as decision`() {
        val entry = ConventionalCommitParser.parse("refactor: extract service layer")
        assertNotNull(entry)
        assertEquals(EntryType.DECISION, entry!!.entryType)
    }

    @Test
    fun `should return null for non-conventional commit`() {
        val entry = ConventionalCommitParser.parse("Update readme with instructions")
        assertNull(entry)
    }

    @Test
    fun `should include commit hash and branch`() {
        val entry = ConventionalCommitParser.parse(
            "feat: add login page",
            commitHash = "abc1234",
            branchName = "feature/login",
            author = "dev@test.com",
        )
        assertNotNull(entry)
        assertEquals("abc1234", entry!!.commitHash)
        assertEquals("feature/login", entry.branchName)
        assertEquals("dev@test.com", entry.author)
    }

    @Test
    fun `should parse multiple commits`() {
        val messages = listOf(
            "feat: add search",
            "not a conventional commit",
            "fix: resolve crash",
            "docs: update API docs",
        )
        val entries = ConventionalCommitParser.parseMultiple(messages)
        assertEquals(3, entries.size)
    }

    @Test
    fun `should handle case insensitive types`() {
        assertTrue(ConventionalCommitParser.isConventionalCommit("FEAT: uppercase"))
        assertTrue(ConventionalCommitParser.isConventionalCommit("Fix: mixed case"))
    }

    @Test
    fun `should handle breaking change marker`() {
        val entry = ConventionalCommitParser.parse("feat!: breaking API change")
        assertNotNull(entry)
        assertEquals("breaking API change", entry!!.text)
    }

    @Test
    fun `should map commit types correctly`() {
        assertEquals(EntryType.INSIGHT, ConventionalCommitParser.mapCommitTypeToEntryType("feat"))
        assertEquals(EntryType.NOTE, ConventionalCommitParser.mapCommitTypeToEntryType("fix"))
        assertEquals(EntryType.NOTE, ConventionalCommitParser.mapCommitTypeToEntryType("docs"))
        assertEquals(EntryType.DECISION, ConventionalCommitParser.mapCommitTypeToEntryType("refactor"))
        assertEquals(EntryType.NOTE, ConventionalCommitParser.mapCommitTypeToEntryType("chore"))
        assertEquals(EntryType.NOTE, ConventionalCommitParser.mapCommitTypeToEntryType("unknown"))
    }
}
