package com.jakubjirak.devjournal.settings

import com.intellij.openapi.options.Configurable
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.JTextField
import javax.swing.SpinnerNumberModel

/**
 * Settings UI for Git-Aware Dev Journal.
 * Available under Settings → Tools → Git-Aware Dev Journal.
 */
class DevJournalConfigurable : Configurable {

    private var panel: JPanel? = null
    private var autoCaptureCheckbox: JCheckBox? = null
    private var standupWindowSpinner: JSpinner? = null
    private var storagePathField: JTextField? = null

    override fun getDisplayName(): String = "Git-Aware Dev Journal"

    override fun createComponent(): JComponent {
        val settings = DevJournalSettings.getInstance()

        panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            insets = Insets(4, 4, 4, 4)
            anchor = GridBagConstraints.WEST
            fill = GridBagConstraints.HORIZONTAL
        }

        autoCaptureCheckbox = JCheckBox("Auto-capture from conventional commits", settings.autoCaptureEnabled)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2
        panel!!.add(autoCaptureCheckbox, gbc)

        gbc.gridwidth = 1

        gbc.gridx = 0; gbc.gridy = 1
        panel!!.add(JLabel("Standup time window (hours):"), gbc)
        standupWindowSpinner = JSpinner(SpinnerNumberModel(settings.standupTimeWindow, 1, 72, 1))
        gbc.gridx = 1
        panel!!.add(standupWindowSpinner, gbc)

        gbc.gridx = 0; gbc.gridy = 2
        panel!!.add(JLabel("Storage path (relative to project):"), gbc)
        storagePathField = JTextField(settings.storagePath, 30)
        gbc.gridx = 1
        panel!!.add(storagePathField, gbc)

        return panel!!
    }

    override fun isModified(): Boolean {
        val settings = DevJournalSettings.getInstance()
        return autoCaptureCheckbox?.isSelected != settings.autoCaptureEnabled ||
            (standupWindowSpinner?.value as? Int) != settings.standupTimeWindow ||
            storagePathField?.text != settings.storagePath
    }

    override fun apply() {
        val settings = DevJournalSettings.getInstance()
        settings.autoCaptureEnabled = autoCaptureCheckbox?.isSelected ?: true
        settings.standupTimeWindow = (standupWindowSpinner?.value as? Int) ?: 24
        settings.storagePath = storagePathField?.text ?: ".dev-journal"
    }

    override fun reset() {
        val settings = DevJournalSettings.getInstance()
        autoCaptureCheckbox?.isSelected = settings.autoCaptureEnabled
        standupWindowSpinner?.value = settings.standupTimeWindow
        storagePathField?.text = settings.storagePath
    }

    override fun disposeUIResources() {
        panel = null
        autoCaptureCheckbox = null
        standupWindowSpinner = null
        storagePathField = null
    }
}
