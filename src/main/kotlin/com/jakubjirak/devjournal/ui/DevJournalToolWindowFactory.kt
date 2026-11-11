package com.jakubjirak.devjournal.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.table.JBTable
import com.jakubjirak.devjournal.model.JournalEntry
import com.jakubjirak.devjournal.narrative.BranchNarrativeBuilder
import com.jakubjirak.devjournal.settings.DevJournalSettings
import com.jakubjirak.devjournal.standup.StandupGenerator
import com.jakubjirak.devjournal.store.JournalRepository
import com.jakubjirak.devjournal.store.JournalStore
import java.awt.BorderLayout
import java.awt.datatransfer.StringSelection
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JPanel
import javax.swing.table.AbstractTableModel

/**
 * Tool window factory that creates the Dev Journal timeline view.
 */
class DevJournalToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        LOG.info("Creating Dev Journal tool window")
        val panel = DevJournalPanel(project)
        val content = ContentFactory.getInstance().createContent(panel, "Dev Journal", false)
        Disposer.register(content) { panel.dispose() }
        toolWindow.contentManager.addContent(content)
    }

    companion object {
        private val LOG = Logger.getInstance(DevJournalToolWindowFactory::class.java)
    }
}

/**
 * Main panel for the Dev Journal tool window with timeline, filters, and search.
 */
class DevJournalPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val tableModel = JournalTableModel()
    private val table = JBTable(tableModel)
    private val branchFilter = JComboBox(arrayOf<Any>("All Branches"))
    private val searchField = JBTextField()
    private val addButton = JButton("Add Entry")
    private val standupButton = JButton("Copy Standup")
    private val narrativeButton = JButton("Copy Branch Summary")
    private val storeListener: () -> Unit = { ApplicationManager.getApplication().invokeLater { refreshData() } }

    init {
        val toolbar = JPanel(BorderLayout())
        val leftPanel = JPanel()
        leftPanel.add(branchFilter)
        leftPanel.add(addButton)
        leftPanel.add(standupButton)
        leftPanel.add(narrativeButton)
        toolbar.add(leftPanel, BorderLayout.WEST)
        toolbar.add(searchField, BorderLayout.CENTER)

        searchField.emptyText.text = "Search entries..."
        standupButton.toolTipText = "Copy a Markdown standup of the entries from the configured time window"
        narrativeButton.toolTipText = "Copy a summary of all entries on the selected branch"

        branchFilter.addActionListener { applyFilters() }
        addButton.addActionListener { AddEntryDialog(project).show() }
        standupButton.addActionListener { copyStandup() }
        narrativeButton.addActionListener { copyBranchSummary() }
        searchField.addActionListener { applyFilters() }

        add(toolbar, BorderLayout.NORTH)
        add(JBScrollPane(table), BorderLayout.CENTER)

        table.autoCreateRowSorter = true
        JournalStore.getInstance(project).addChangeListener(storeListener)
        refreshData()
    }

    /** Detaches the store listener; called when the tool window content is disposed. */
    fun dispose() {
        JournalStore.getInstance(project).removeChangeListener(storeListener)
    }

    fun refreshData() {
        if (project.isDisposed) return
        val allEntries = JournalStore.getInstance(project).getAllEntries()
        updateBranchFilter(allEntries)
        applyFilters()
    }

    private fun updateBranchFilter(entries: List<JournalEntry>) {
        val selected = branchFilter.selectedItem
        val branches = entries.mapNotNull { it.branchName }.distinct().sorted()
        branchFilter.removeAllItems()
        branchFilter.addItem(ALL_BRANCHES)
        branches.forEach { branchFilter.addItem(it) }
        branchFilter.selectedItem = if (selected in branches) selected else ALL_BRANCHES
    }

    private fun selectedBranch(): String? = (branchFilter.selectedItem as? String)?.takeIf { it != ALL_BRANCHES }

    private fun applyFilters() {
        val branch = selectedBranch()
        val query = searchField.text.orEmpty()
        val results = JournalStore.getInstance(project).getAllEntries()
            .filter { branch == null || it.branchName == branch }
            .filter { JournalRepository.matches(it, query) }
        tableModel.setData(results)
    }

    private fun copyStandup() {
        val hours = DevJournalSettings.getInstance().standupTimeWindow
        copy(StandupGenerator.generate(JournalStore.getInstance(project).getAllEntries(), hours), "Standup")
    }

    private fun copyBranchSummary() {
        val branch = selectedBranch()
        if (branch == null) {
            Messages.showInfoMessage(project, "Select a branch in the branch filter first.", "Branch Summary")
            return
        }
        val narrative = BranchNarrativeBuilder.build(branch, JournalStore.getInstance(project).getAllEntries())
        copy(narrative.summary, "Branch summary")
    }

    private fun copy(text: String, what: String) {
        CopyPasteManager.getInstance().setContents(StringSelection(text))
        Messages.showInfoMessage(project, "$what copied to the clipboard.", "Dev Journal")
    }

    companion object {
        private const val ALL_BRANCHES = "All Branches"
    }
}

/**
 * Table model for displaying journal entries.
 */
class JournalTableModel : AbstractTableModel() {

    private var data: List<JournalEntry> = emptyList()

    fun setData(entries: List<JournalEntry>) {
        data = entries.sortedByDescending { it.timestamp }
        fireTableDataChanged()
    }

    override fun getRowCount(): Int = data.size

    override fun getColumnCount(): Int = COLUMNS.size

    override fun getColumnName(column: Int): String = COLUMNS[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val entry = data[rowIndex]
        return when (columnIndex) {
            COL_TYPE -> entry.entryType.label
            COL_TEXT -> entry.text
            COL_BRANCH -> entry.branchName ?: ""
            COL_COMMIT -> entry.commitHash?.take(SHORT_HASH_LENGTH) ?: ""
            COL_FILE -> entry.filePath ?: ""
            COL_TIMESTAMP -> formatTimestamp(entry.timestamp)
            COL_TAGS -> entry.tags.joinToString(", ")
            else -> ""
        }
    }

    fun getEntryAt(row: Int): JournalEntry? {
        return if (row in data.indices) data[row] else null
    }

    private fun formatTimestamp(millis: Long): String {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }

    companion object {
        private const val COL_TYPE = 0
        private const val COL_TEXT = 1
        private const val COL_BRANCH = 2
        private const val COL_COMMIT = 3
        private const val COL_FILE = 4
        private const val COL_TIMESTAMP = 5
        private const val COL_TAGS = 6
        private const val SHORT_HASH_LENGTH = 7

        private val COLUMNS = arrayOf("Type", "Entry", "Branch", "Commit", "File", "Timestamp", "Tags")
    }
}
