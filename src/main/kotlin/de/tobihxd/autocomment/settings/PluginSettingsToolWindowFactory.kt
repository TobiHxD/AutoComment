package de.tobihxd.autocomment.settings;

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import de.tobihxd.autocomment.LmStudioClient
import java.awt.Component
import java.awt.Dimension
import java.util.Locale
import javax.swing.*

class PluginSettingsToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

        val settings = PluginSettings.getInstance(project)

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        fun fitHeight(c: JComponent) {
            val pref = c.preferredSize
            c.maximumSize = Dimension(Int.MAX_VALUE, pref.height)
        }

        fun addLabeledField(labelText: String, field: JComponent) {
            val label = JBLabel(labelText)
            label.alignmentX = Component.LEFT_ALIGNMENT
            field.alignmentX = Component.LEFT_ALIGNMENT

            panel.add(label)
            panel.add(field)
            panel.add(Box.createVerticalStrut(5))
        }

        // === API / LM Studio Settings ===

        val urlField = JBTextField(settings.state.serverUrl)
        fitHeight(urlField)
        urlField.document.addDocumentListener(SimpleDocumentListener {
            settings.state.serverUrl = urlField.text
        })
        addLabeledField("Server URL:", urlField)

        // === Model ComboBox ===

        val modelCombo = JComboBox<String>().apply {
            isEnabled = false
            addItem("Loading models…")
        }
        fitHeight(modelCombo)

        modelCombo.addActionListener {
            val selected = modelCombo.selectedItem as? String
            if (!selected.isNullOrBlank() && selected != "Loading models…") {
                settings.state.model = selected
            }
        }
        addLabeledField("Modell:", modelCombo)

        // async laden
        loadDataAsync(project, modelCombo)

        val connectTimeoutSpinner =
            JSpinner(SpinnerNumberModel(settings.state.connectTimeoutSec, 1, 300, 1))
        fitHeight(connectTimeoutSpinner)
        connectTimeoutSpinner.addChangeListener {
            settings.state.connectTimeoutSec = connectTimeoutSpinner.value as Int
        }
        addLabeledField("Connect Timeout (s):", connectTimeoutSpinner)

        val writeTimeoutSpinner =
            JSpinner(SpinnerNumberModel(settings.state.writeTimeoutSec, 1, 300, 1))
        fitHeight(writeTimeoutSpinner)
        writeTimeoutSpinner.addChangeListener {
            settings.state.writeTimeoutSec = writeTimeoutSpinner.value as Int
        }
        addLabeledField("Write Timeout (s):", writeTimeoutSpinner)

        val readTimeoutSpinner =
            JSpinner(SpinnerNumberModel(settings.state.readTimeoutSec, 1, 600, 1))
        fitHeight(readTimeoutSpinner)
        readTimeoutSpinner.addChangeListener {
            settings.state.readTimeoutSec = readTimeoutSpinner.value as Int
        }
        addLabeledField("Read Timeout (s):", readTimeoutSpinner)

        val detailCombo = ComboBox(arrayOf("low", "middle", "high"))
        fitHeight(detailCombo)
        detailCombo.selectedItem = settings.state.detailLevel
        detailCombo.addActionListener {
            settings.state.detailLevel = detailCombo.selectedItem as String
        }
        addLabeledField("Detaillevel:", detailCombo)

        val localeCombo = ComboBox(settings.state.localeList)
        fitHeight(localeCombo)
        localeCombo.selectedItem = settings.state.locale
        localeCombo.addActionListener {
            settings.state.locale = localeCombo.selectedItem as Locale
        }
        addLabeledField("Locale:", localeCombo)

        panel.add(Box.createVerticalGlue())

        val content = ContentFactory.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun loadDataAsync(
        project: Project,
        comboBox: JComboBox<String>
    ) {
        object : Task.Backgroundable(project, "Loading models", false) {

            private lateinit var items: List<String>

            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true

                val client = LmStudioClient(
                    project = project
                )

                items = client.getModels()
            }

            override fun onSuccess() {
                comboBox.removeAllItems()
                items.forEach { comboBox.addItem(it) }
                comboBox.isEnabled = true
            }

            override fun onThrowable(error: Throwable) {
                comboBox.removeAllItems()
                error.printStackTrace();
                comboBox.addItem("Error loading models")
                comboBox.isEnabled = false
            }
        }.queue()
    }


    override fun shouldBeAvailable(project: Project): Boolean = true
}
