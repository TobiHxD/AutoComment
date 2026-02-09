package de.tobihxd.autocomment

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethod

class GenerateJavadocsTask(
    private val project: Project,
    private val methods: List<PsiMethod>,
    private val lmClient: LmStudioClient,
    private val inserter: CommentInserter
) : Task.Backgroundable(
    project,
    "Generating Javadocs",
    true // cancellable
) {

    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = false

        val sortedMethods = methods.sortedByDescending { it.textOffset }
        val total = sortedMethods.size
        var done = 0

        for (method in sortedMethods) {

            if (indicator.isCanceled) break

            indicator.text = "Processing ${method.name}()"
            indicator.text2 = "${done + 1} / $total"
            indicator.fraction = done.toDouble() / total.toDouble()

            try {
                // --- PSI-Lesezugriff in ReadAction ---
                val methodText: String = ReadAction.compute<String, RuntimeException> {
                    method.text // alles, was PSI liest
                }

                // LM Client Request (keine PSI-Operationen, kann normal laufen)
                val comment = lmClient.generateJavadocForMethod(methodText, indicator)
                    ?: continue

                // --- Kommentar einfügen (WriteAction) ---
                inserter.insertComment(project, method, comment)

            } catch (e: java.net.SocketException) {
                if (e.message == "Socket closed") {
                    break // Task abgebrochen
                } else {
                    throw e
                }
            } catch (e: Exception) {
                e.printStackTrace()
                indicator.text = "Error generating comment for ${method.name}()"
            }

            done++
            indicator.fraction = done.toDouble() / total.toDouble()
        }

        indicator.fraction = 1.0
    }
}
