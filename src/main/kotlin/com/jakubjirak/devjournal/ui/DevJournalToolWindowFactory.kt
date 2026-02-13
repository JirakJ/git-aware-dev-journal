package com.jakubjirak.devjournal.ui

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.table.JBTable
import com.jakubjirak.devjournal.model.JournalEntry
import com.jakubjirak.devjournal.store.JournalStore
import java.awt.BorderLayout
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

    init {
        val toolbar = JPanel(BorderLayout())
        val leftPanel = JPanel()
        leftPanel.add(branchFilter)
        leftPanel.add(addButton)
        toolbar.add(leftPanel, BorderLayout.WEST)
        toolbar.add(searchField, BorderLayout.CENTER)

        searchField.emptyText.text = "Search entries..."

        branchFilter.addActionListener { applyFilters() }
        addButton.addActionListener { showAddEntryDialog() }
        searchField.addActionListener { applyFilters() }

        add(toolbar, BorderLayout.NORTH)
        add(JBScrollPane(table), BorderLayout.CENTER)

        table.autoCreateRowSorter = true
        refreshData()
    }

    fun refreshData() {
        val store = JournalStore.getInstance(project)
        val allEntries = store.getAllEntries()
        tableModel.setData(allEntries)
        updateBranchFilter(allEntries)
    }

    private fun updateBranchFilter(entries: List<JournalEntry>) {
        val branches = entries.mapNotNull { it.branchName }.distinct().sorted()
        branchFilter.removeAllItems()
        branchFilter.addItem("All Branches")
        branches.forEach { branchFilter.addItem(it) }
    }

    private fun applyFilters() {
        val store = JournalStore.getInstance(project)
        var results = store.getAllEntries()

        val selectedBranch = branchFilter.selectedItem as? String
        if (selectedBranch != null && selectedBranch != "All Branches") {
            results = results.filter { it.branchName == selectedBranch }
        }

        val query = searchField.text?.trim()
        if (!query.isNullOrEmpty()) {
            val lowerQuery = query.lowercase()
            results = results.filter {
                it.text.lowercase().contains(lowerQuery) ||
                    it.tags.any { tag -> tag.lowercase().contains(lowerQuery) }
            }
        }

        tableModel.setData(results)
    }

    private fun showAddEntryDialog() {
        val dialog = AddEntryDialog(project)
        if (dialog.showAndGet()) {
            refreshData()
        }
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
