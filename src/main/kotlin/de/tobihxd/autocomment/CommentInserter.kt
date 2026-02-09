package de.tobihxd.autocomment;

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethod
import org.json.JSONObject

class CommentInserter {

    fun insertComment(
        project: Project,
        method: PsiMethod,
        javadocText: String
    ) {
        WriteCommandAction.runWriteCommandAction(project) {

            val factory = JavaPsiFacade.getElementFactory(project)
            val jsonObject = JSONObject(javadocText)
            val comment  = buildString(jsonObject)
            val newDocComment = factory.createDocCommentFromText(comment)

            val existing = method.docComment
            if (existing != null) {
                existing.replace(newDocComment)
            } else {
                method.addBefore(newDocComment, method.firstChild)
            }
        }
    }

    fun buildString(jsonObject: JSONObject): String{
        val text = jsonObject.getString("text")
        val params = jsonObject.getJSONArray("params")
        val returns = jsonObject.optString("returns")
        val throws = jsonObject.getJSONArray("throws")
        val sb = StringBuilder()
        sb.append("/**\n")

        // Beschreibung
        sb.append(" * ").append(text).append("\n")

        // Parameter
        for (i in 0 until params.length()) {
            val param = params.getJSONObject(i)
            val paramName = param.getString("name")
            val paramDesc = param.getString("description")
            sb.append(" * @param ").append(paramName).append(" ").append(paramDesc).append("\n")
        }

        // Rückgabewert
        if (returns.isNotBlank()) {
            sb.append(" * @return ").append(returns).append("\n")
        }

        // Exceptions
        for (i in 0 until throws.length()) {
            val exceptionName = throws.getString(i)
            sb.append(" * @throws ").append(exceptionName).append("\n")
        }

        sb.append(" */")

        return sb.toString();
    }
}
