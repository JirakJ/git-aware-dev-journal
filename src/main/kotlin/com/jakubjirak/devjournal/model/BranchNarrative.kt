package com.jakubjirak.devjournal.model

/**
 * Represents a narrative summary of work done on a branch.
 */
data class BranchNarrative(
    val branchName: String,
    val entries: List<JournalEntry>,
    val summary: String,
    val createdAt: Long = System.currentTimeMillis(),
    val mergedAt: Long? = null,
)
