package com.jakubjirak.devjournal.model

/**
 * A single journal entry linked to git context and code location.
 */
data class JournalEntry(
    val id: String,
    val text: String,
    val entryType: EntryType,
    val commitHash: String? = null,
    val branchName: String? = null,
    val filePath: String? = null,
    val lineNumber: Int? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val author: String? = null,
    val tags: List<String> = emptyList(),
)
