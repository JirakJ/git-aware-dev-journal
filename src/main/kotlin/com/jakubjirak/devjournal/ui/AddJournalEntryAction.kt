package com.jakubjirak.devjournal.ui

import com.jakubjirak.devjournal.store.JournalRepository
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ActionUpdateThread

/**
 * Action to add a journal entry at the current cursor position.
 * Accessible via Ctrl+Shift+J or the Tools menu.
 */
class AddJournalEntryAction : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT


    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR)
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)

        val filePath = file?.path?.let { JournalRepository.relativePath(project.basePath, it) }

        val lineNumber = editor?.let {
            it.caretModel.logicalPosition.line + 1
        }

        val dialog = AddEntryDialog(
            project = project,
            prefilledFilePath = filePath,
            prefilledLineNumber = lineNumber,
        )

        // The tool window listens to JournalStore changes, so no explicit refresh is needed.
        dialog.show()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}
