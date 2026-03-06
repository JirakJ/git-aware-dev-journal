package com.jakubjirak.devjournal.ui

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.jakubjirak.devjournal.git.GitContextProvider
import com.jakubjirak.devjournal.model.EntryType
import com.jakubjirak.devjournal.store.JournalStore
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.JTextField

/**
 * Dialog for adding a new journal entry with type, text, and tags.
 */
class AddEntryDialog(
    private val project: Project,
    private val prefilledFilePath: String? = null,
    private val prefilledLineNumber: Int? = null,
) : DialogWrapper(project) {

    private val textArea = JTextArea(4, 40)
    private val typeCombo = JComboBox(EntryType.entries.toTypedArray())
    private val tagsField = JTextField(30)

    init {
        title = "Add Journal Entry"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            insets = Insets(4, 4, 4, 4)
            anchor = GridBagConstraints.WEST
            fill = GridBagConstraints.HORIZONTAL
        }

        gbc.gridx = 0; gbc.gridy = 0
        panel.add(JLabel("Entry type:"), gbc)
        gbc.gridx = 1
        panel.add(typeCombo, gbc)

        gbc.gridx = 0; gbc.gridy = 1
        panel.add(JLabel("Entry text:"), gbc)
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0
        panel.add(textArea, gbc)

        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0.0
        gbc.gridx = 0; gbc.gridy = 2
        panel.add(JLabel("Tags (comma-separated):"), gbc)
        gbc.gridx = 1
        panel.add(tagsField, gbc)

        if (prefilledFilePath != null) {
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2
            val locationText = "$prefilledFilePath${prefilledLineNumber?.let { ":$it" } ?: ""}"
            panel.add(JLabel("Location: $locationText"), gbc)
        }

        return panel
    }

    override fun doOKAction() {
        val text = textArea.text?.trim()
        if (text.isNullOrEmpty()) {
            return
        }

        val entryType = typeCombo.selectedItem as EntryType
        val tags = tagsField.text
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        var branchName: String? = null
        var commitHash: String? = null
        try {
            val gitProvider = GitContextProvider.getInstance(project)
            branchName = gitProvider.getCurrentBranch()
            commitHash = gitProvider.getHeadCommitHash()
        } catch (_: Exception) {
            // Git4Idea not available
        }

        val store = JournalStore.getInstance(project)
        store.newEntry(
            text = text,
            entryType = entryType,
            context = JournalStore.EntryContext(
                commitHash = commitHash,
                branchName = branchName,
                filePath = prefilledFilePath,
                lineNumber = prefilledLineNumber,
                tags = tags,
            ),
        )

        super.doOKAction()
    }

    companion object {
        private val LOG = Logger.getInstance(AddEntryDialog::class.java)
    }
}
