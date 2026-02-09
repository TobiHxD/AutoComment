package de.tobihxd.autocomment.settings;

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@State(
    name = "JavadocPluginSettings",
    storages = [Storage("JavadocPluginSettings.xml")]
)
@Service(Service.Level.PROJECT) // Projektbezogen
class PluginSettings : PersistentStateComponent<PluginSettings.State> {

    data class State(
        var serverUrl: String = "http://localhost:1234",
        var connectTimeoutSec: Int = 60,
        var writeTimeoutSec: Int = 60,
        var readTimeoutSec: Int = 180,
        var model: String = "No model found",

        var detailLevel: String = "Kurz",
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(project: Project): PluginSettings =
            project.getService(PluginSettings::class.java)
    }
}