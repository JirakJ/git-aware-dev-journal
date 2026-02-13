package com.jakubjirak.devjournal.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

/**
 * Persistent settings for the Git-Aware Dev Journal plugin.
 */
@State(
    name = "DevJournalSettings",
    storages = [Storage("devJournalSettings.xml")]
)
class DevJournalSettings : PersistentStateComponent<DevJournalSettings.State> {

    data class State(
        var autoCaptureEnabled: Boolean = true,
        var standupTimeWindow: Int = 24,
        var storagePath: String = ".dev-journal",
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var autoCaptureEnabled: Boolean
        get() = state.autoCaptureEnabled
        set(value) { state.autoCaptureEnabled = value }

    var standupTimeWindow: Int
        get() = state.standupTimeWindow
        set(value) { state.standupTimeWindow = value }

    var storagePath: String
        get() = state.storagePath
        set(value) { state.storagePath = value }

    companion object {
        fun getInstance(): DevJournalSettings =
            ApplicationManager.getApplication().getService(DevJournalSettings::class.java)
    }
}
