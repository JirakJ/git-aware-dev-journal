package com.jakubjirak.devjournal.store

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.jakubjirak.devjournal.model.EntryType
import com.jakubjirak.devjournal.model.JournalEntry
import com.jakubjirak.devjournal.settings.DevJournalSettings
import java.io.File
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Project-level service that manages journal entries.
 * Persists entries to `<storagePath>/entries.json` (default `.dev-journal/entries.json`) in the project root.
 */
@Service(Service.Level.PROJECT)
class JournalStore(private val project: Project) {

    private val repository = JournalRepository(storageFile())
    private val listeners = CopyOnWriteArrayList<() -> Unit>()

    init {
        synchronized(repository) {
            repository.load()
            logError()
        }
    }

    /** Registers a callback invoked (on the calling thread) after every change. */
    fun addChangeListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun removeChangeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    fun addEntry(entry: JournalEntry) = mutate { repository.add(entry) }

    fun getAllEntries(): List<JournalEntry> = synchronized(repository) { repository.all() }

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

    private fun mutate(change: () -> Unit) {
        synchronized(repository) {
            change()
            logError()
        }
        listeners.forEach { it() }
    }

    private fun logError() {
        repository.lastError?.let { LOG.warn(it) }
    }

    private fun storageFile(): File =
        JournalRepository.resolveStorageFile(project.basePath, DevJournalSettings.getInstance().storagePath)

    companion object {
        private val LOG = Logger.getInstance(JournalStore::class.java)

        fun getInstance(project: Project): JournalStore =
            project.getService(JournalStore::class.java)
    }
}
