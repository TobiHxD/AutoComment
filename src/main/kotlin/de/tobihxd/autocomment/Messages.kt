package de.tobihxd.autocomment

import com.intellij.openapi.project.Project
import de.tobihxd.autocomment.settings.PluginSettings
import java.util.Locale
import java.util.ResourceBundle

object Messages{
    private const val BASE_NAME = "META-INF.plugin.messages.MessagesBundle"

    fun message(key: String,project: Project, locale: Locale = PluginSettings.getInstance(project).state.locale, vararg params: Any): String {
        println(locale)
        val bundle = ResourceBundle.getBundle(BASE_NAME, locale)
        return if (params.isEmpty()) {
            bundle.getString(key)
        } else {
            String.format(bundle.getString(key), *params)
        }
    }
}