package com.jakubjirak.devjournal.git

import com.jakubjirak.devjournal.model.EntryType
import com.jakubjirak.devjournal.model.JournalEntry
import java.util.UUID

/**
 * Parses conventional commit messages and creates journal entries.
 * Supports: feat:, fix:, docs:, refactor:, chore:
 */
object ConventionalCommitParser {

    private val CONVENTIONAL_PATTERN = Regex(
        """^(feat|fix|docs|refactor|chore)(\(.+?\))?!?:\s*(.+)$""",
        RegexOption.IGNORE_CASE,
    )

    fun isConventionalCommit(message: String): Boolean {
        val firstLine = message.lines().firstOrNull()?.trim() ?: return false
        return CONVENTIONAL_PATTERN.matches(firstLine)
    }

    fun parse(
        commitMessage: String,
        commitHash: String? = null,
        branchName: String? = null,
        author: String? = null,
    ): JournalEntry? {
        val firstLine = commitMessage.lines().firstOrNull()?.trim() ?: return null
        val match = CONVENTIONAL_PATTERN.matchEntire(firstLine) ?: return null

        val type = match.groupValues[1].lowercase()
        val scope = match.groupValues[2].removePrefix("(").removeSuffix(")")
        val description = match.groupValues[3].trim()

        val entryType = mapCommitTypeToEntryType(type)
        val tags = buildList {
            add(type)
            if (scope.isNotEmpty()) add(scope)
        }

        return JournalEntry(
            id = UUID.randomUUID().toString(),
            text = description,
            entryType = entryType,
            commitHash = commitHash,
            branchName = branchName,
            author = author,
            tags = tags,
        )
    }

    fun parseMultiple(
        commitMessages: List<String>,
        branchName: String? = null,
        author: String? = null,
    ): List<JournalEntry> {
        return commitMessages.mapNotNull { parse(it, branchName = branchName, author = author) }
    }

    internal fun mapCommitTypeToEntryType(type: String): EntryType {
        return when (type.lowercase()) {
            "feat" -> EntryType.INSIGHT
            "fix" -> EntryType.NOTE
            "docs" -> EntryType.NOTE
            "refactor" -> EntryType.DECISION
            "chore" -> EntryType.NOTE
            else -> EntryType.NOTE
        }
    }
}
