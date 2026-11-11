package com.jakubjirak.devjournal

import com.jakubjirak.devjournal.model.EntryType
import com.jakubjirak.devjournal.model.JournalEntry
import com.jakubjirak.devjournal.settings.DevJournalSettings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ModelTest {

    @Test
    fun `there are six entry types with distinct labels`() {
        assertEquals(6, EntryType.entries.size)
        assertEquals(EntryType.entries.size, EntryType.entries.map { it.label }.toSet().size)
        assertEquals("TODO", EntryType.TODO.label)
    }

    @Test
    fun `journal entry defaults are empty`() {
        val e = JournalEntry(id = "x", text = "t", entryType = EntryType.NOTE)
        assertNull(e.commitHash)
        assertNull(e.branchName)
        assertNull(e.filePath)
        assertNull(e.lineNumber)
        assertTrue(e.tags.isEmpty())
    }

    @Test
    fun `settings state defaults`() {
        val state = DevJournalSettings.State()
        assertEquals(24, state.standupTimeWindow)
        assertEquals(".dev-journal", state.storagePath)
    }
}
