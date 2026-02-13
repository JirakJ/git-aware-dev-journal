package com.jakubjirak.devjournal.model

/**
 * Types of journal entries.
 */
enum class EntryType(val label: String) {
    NOTE("Note"),
    DECISION("Decision"),
    TODO("TODO"),
    BLOCKER("Blocker"),
    INSIGHT("Insight"),
    QUESTION("Question"),
}
