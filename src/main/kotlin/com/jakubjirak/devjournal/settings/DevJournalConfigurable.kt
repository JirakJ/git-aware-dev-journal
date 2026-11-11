package com.jakubjirak.devjournal.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.*

/**
 * Settings UI for Git-Aware Dev Journal.
 * Available under Settings → Tools → Git-Aware Dev Journal.
 */
class DevJournalConfigurable : BoundConfigurable("Git-Aware Dev Journal") {

    override fun createPanel() = panel {
        val settings = DevJournalSettings.getInstance()

        group("General") {
            row("Standup time window (hours):") {
                spinner(1..168)
                    .bindIntValue(settings::standupTimeWindow)
                    .comment("How far back the tool window's Copy Standup button looks (max 1 week)")
            }
        }

        group("Storage") {
            row("Journal directory:") {
                cell(TextFieldWithBrowseButton().apply {
                    addBrowseFolderListener(
                        com.intellij.openapi.ui.TextBrowseFolderListener(
                            FileChooserDescriptorFactory.createSingleFolderDescriptor()
                                .withTitle("Select Journal Directory"), null))
                })
                    .bindText(settings::storagePath)
                    .comment("Relative to the project root, or absolute. Takes effect after reopening the project.")
            }
        }
    }
}
