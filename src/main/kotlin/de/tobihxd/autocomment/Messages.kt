package de.tobihxd.autocomment

import com.intellij.AbstractBundle
import org.jetbrains.annotations.PropertyKey

object Messages : AbstractBundle("META-INF.plugin.MessagesBundle") {
    fun message(@PropertyKey(resourceBundle = "META-INF.plugin.MessagesBundle") key: String, vararg params: Any): String {
        return getMessage(key, *params)
    }
}