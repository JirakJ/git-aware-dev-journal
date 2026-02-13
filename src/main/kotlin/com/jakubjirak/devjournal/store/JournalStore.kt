package com.jakubjirak.devjournal.store

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.jakubjirak.devjournal.model.EntryType
import com.jakubjirak.devjournal.model.JournalEntry
import com.jakubjirak.devjournal.settings.DevJournalSettings
import java.io.File
import java.util.UUID

/**
 * Project-level service that manages journal entries.
 * Persists entries to `.dev-journal/entries.json` in the project root.
 */
@Service(Service.Level.PROJECT)
class JournalStore(private val project: Project) {

    private val entries = mutableListOf<JournalEntry>()
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    init {
        loadEntries()
    }

    fun addEntry(entry: JournalEntry) {
        entries.add(entry)
        saveEntries()
        LOG.info("Added journal entry: ${entry.id}")
    }

    fun removeEntry(id: String) {
        entries.removeAll { it.id == id }
        saveEntries()
    }

    fun updateEntry(updated: JournalEntry) {
        val index = entries.indexOfFirst { it.id == updated.id }
        if (index >= 0) {
            entries[index] = updated
            saveEntries()
        }
    }

    fun getAllEntries(): List<JournalEntry> = entries.toList()

    fun getEntriesByBranch(branchName: String): List<JournalEntry> =
        entries.filter { it.branchName == branchName }

    fun getEntriesByType(type: EntryType): List<JournalEntry> =
        entries.filter { it.entryType == type }

    fun getEntriesSince(timestampMillis: Long): List<JournalEntry> =
        entries.filter { it.timestamp >= timestampMillis }

    fun searchEntries(query: String): List<JournalEntry> {
        val lowerQuery = query.lowercase()
        return entries.filter {
            it.text.lowercase().contains(lowerQuery) ||
                it.tags.any { tag -> tag.lowercase().contains(lowerQuery) }
        }
    }

    fun createEntry(entry: JournalEntry): JournalEntry {
        addEntry(entry)
        return entry
    }

    fun newEntry(
        text: String,
        entryType: EntryType,
        context: EntryContext = EntryContext(),
    ): JournalEntry {
        val entry = JournalEntry(
            id = UUID.randomUUID().toString(),
            text = text,
            entryType = entryType,
            commitHash = context.commitHash,
            branchName = context.branchName,
            filePath = context.filePath,
            lineNumber = context.lineNumber,
            author = context.author,
            tags = context.tags,
        )
        addEntry(entry)
        return entry
    }

    /**
     * Context information for a new journal entry.
     */
    data class EntryContext(
        val commitHash: String? = null,
        val branchName: String? = null,
        val filePath: String? = null,
        val lineNumber: Int? = null,
        val author: String? = null,
        val tags: List<String> = emptyList(),
    )

    fun getEntryCount(): Int = entries.size

    private fun getStorageFile(): File {
        val basePath = project.basePath ?: return File(".dev-journal/entries.json")
        val storagePath = DevJournalSettings.getInstance().storagePath
        return File(basePath, "$storagePath/entries.json")
    }

    private fun loadEntries() {
        try {
            val file = getStorageFile()
            if (file.exists()) {
                val json = file.readText()
                val type = object : TypeToken<List<JournalEntry>>() {}.type
                val loaded: List<JournalEntry> = gson.fromJson(json, type)
                entries.clear()
                entries.addAll(loaded)
                LOG.info("Loaded ${entries.size} journal entries")
            }
        } catch (e: Exception) {
            LOG.warn("Failed to load journal entries", e)
        }
    }

    private fun saveEntries() {
        try {
            val file = getStorageFile()
            file.parentFile?.mkdirs()
            file.writeText(gson.toJson(entries))
        } catch (e: Exception) {
            LOG.warn("Failed to save journal entries", e)
        }
    }

    companion object {
        private val LOG = Logger.getInstance(JournalStore::class.java)

        fun getInstance(project: Project): JournalStore =
            project.getService(JournalStore::class.java)
    }
}
