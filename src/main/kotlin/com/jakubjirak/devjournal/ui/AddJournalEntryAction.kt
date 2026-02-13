package com.jakubjirak.devjournal.ui

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

/**
 * Action to add a journal entry at the current cursor position.
 * Accessible via Ctrl+Shift+J or the Tools menu.
 */
class AddJournalEntryAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR)
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)

        val filePath = file?.path?.let { path ->
            val basePath = project.basePath
            if (basePath != null && path.startsWith(basePath)) {
                path.removePrefix(basePath).removePrefix("/")
            } else {
                path
            }
        }

        val lineNumber = editor?.let {
            it.caretModel.logicalPosition.line + 1
        }

        val dialog = AddEntryDialog(
            project = project,
            prefilledFilePath = filePath,
            prefilledLineNumber = lineNumber,
        )

        if (dialog.showAndGet()) {
            // Entry was added via the dialog — refresh tool window if open
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}
