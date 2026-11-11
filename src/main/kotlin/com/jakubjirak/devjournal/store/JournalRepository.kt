/*
 * Copyright 2026 Jakub Jirák
 * SPDX-License-Identifier: Apache-2.0
 */
package com.jakubjirak.devjournal.store

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.jakubjirak.devjournal.model.EntryType
import com.jakubjirak.devjournal.model.JournalEntry
import java.io.File
import java.io.IOException

/**
 * Platform-independent journal storage: an in-memory list of entries persisted as pretty-printed
 * JSON to [file]. [JournalStore] wraps this for the IDE; tests use it directly.
 *
 * Not thread-safe on its own — callers serialize access ([JournalStore] synchronizes).
 */
class JournalRepository(private val file: File) {

    private val entries = mutableListOf<JournalEntry>()

    /** Error message of the last failed load/save, or `null` when the last I/O succeeded. */
    var lastError: String? = null
        private set

    fun load() {
        entries.clear()
        lastError = null
        if (!file.isFile) return
        try {
            val loaded: List<JournalEntry?>? = GSON.fromJson(file.readText(), LIST_TYPE)
            loaded.orEmpty().mapNotNullTo(entries) { sanitize(it) }
        } catch (e: IOException) {
            lastError = "Failed to read ${file.path}: ${e.message}"
        } catch (e: JsonParseException) {
            lastError = "Corrupt journal file ${file.path}: ${e.message}"
        }
    }

    fun add(entry: JournalEntry) {
        entries.add(entry)
        save()
    }

    fun remove(id: String): Boolean {
        val removed = entries.removeAll { it.id == id }
        if (removed) save()
        return removed
    }

    fun update(updated: JournalEntry): Boolean {
        val index = entries.indexOfFirst { it.id == updated.id }
        if (index < 0) return false
        entries[index] = updated
        save()
        return true
    }

    fun all(): List<JournalEntry> = entries.toList()

    fun count(): Int = entries.size

    fun byBranch(branchName: String): List<JournalEntry> = entries.filter { it.branchName == branchName }

    fun byType(type: EntryType): List<JournalEntry> = entries.filter { it.entryType == type }

    fun since(timestampMillis: Long): List<JournalEntry> = entries.filter { it.timestamp >= timestampMillis }

    fun search(query: String): List<JournalEntry> = entries.filter { matches(it, query) }

    private fun save() {
        lastError = null
        try {
            file.parentFile?.mkdirs()
            file.writeText(GSON.toJson(entries))
        } catch (e: IOException) {
            lastError = "Failed to write ${file.path}: ${e.message}"
        }
    }

    companion object {
        private val GSON: Gson = GsonBuilder().setPrettyPrinting().create()
        private val LIST_TYPE = object : TypeToken<List<JournalEntry?>>() {}.type
        const val DEFAULT_DIR = ".dev-journal"
        const val FILE_NAME = "entries.json"

        /**
         * Resolves the journal file: [storagePath] may be relative to the project root or absolute
         * (the settings folder chooser yields absolute paths); blank falls back to [DEFAULT_DIR].
         */
        fun resolveStorageFile(projectBasePath: String?, storagePath: String): File {
            val dir = File(storagePath.trim().ifEmpty { DEFAULT_DIR })
            val resolvedDir = when {
                dir.isAbsolute -> dir
                projectBasePath != null -> File(projectBasePath, dir.path)
                else -> dir
            }
            return File(resolvedDir, FILE_NAME)
        }

        /** Splits a comma-separated tag field into trimmed, non-empty, de-duplicated tags. */
        fun parseTags(raw: String?): List<String> =
            raw.orEmpty().split(",").map { it.trim() }.filter { it.isNotEmpty() }.distinct()

        /**
         * Returns [path] relative to [basePath] when it lies inside it (on a path-segment boundary),
         * otherwise [path] unchanged. Separators are normalised to `/`.
         */
        fun relativePath(basePath: String?, path: String): String {
            val normalized = path.replace('\\', '/')
            val base = basePath?.replace('\\', '/')?.trimEnd('/') ?: return normalized
            val inside = base.isNotEmpty() && normalized.startsWith("$base/")
            return if (inside) normalized.substring(base.length + 1) else normalized
        }

        /** Case-insensitive match on entry text or any tag; a blank query matches everything. */
        fun matches(entry: JournalEntry, query: String): Boolean {
            val q = query.trim().lowercase()
            if (q.isEmpty()) return true
            return entry.text.lowercase().contains(q) || entry.tags.any { it.lowercase().contains(q) }
        }

        /**
         * Gson bypasses Kotlin constructors, so hand-edited or older files can yield `null` in
         * non-null fields. Drop entries without id/text/type and default a missing tag list.
         */
        @Suppress("SENSELESS_COMPARISON", "USELESS_ELVIS")
        internal fun sanitize(entry: JournalEntry?): JournalEntry? {
            if (entry == null) return null
            val complete = entry.id != null && entry.text != null && entry.entryType != null
            if (!complete) return null
            val tags = (entry.tags ?: emptyList()).filterNotNull()
            return if (tags == entry.tags) entry else entry.copy(tags = tags)
        }
    }
}
