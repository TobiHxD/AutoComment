package de.tobihxd.autocomment;

import com.intellij.codeInsight.generation.PsiMethodMember
import com.intellij.ide.util.MemberChooser
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import de.tobihxd.autocomment.settings.PluginSettings

class GenerateCommentsAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE)
        // Aktiv nur für Java-Dateien
        e.presentation.isEnabledAndVisible = file is PsiJavaFile
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT // Background Thread
    }


    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return

        // Alle Methoden sammeln
        val allMethods: List<PsiMethod> = MethodCollector().collectAllMethods(psiFile)
        if (allMethods.isEmpty()) {
            Messages.showInfoMessage(project, "Keine Methoden gefunden.", "Info")
            return
        }

        // PsiMethodMember-Array erstellen
        val members: Array<PsiMethodMember> = allMethods.map { method ->
            PsiMethodMember(method)
        }.toTypedArray()

        // MemberChooser anzeigen
        val chooser = MemberChooser(members, false, true, project)
        chooser.title = "Select methods to generate Javadocs"
        chooser.show()

        val selectedMethods = chooser.selectedElements?.map { it.element } ?: return

        // PluginSettings abrufen
        val settings = PluginSettings.getInstance(project)

        // LM Studio Client & Inserter initialisieren
        val lmClient = LmStudioClient(project)
        val inserter = CommentInserter()

        // Task starten
        GenerateJavadocsTask(project, selectedMethods, lmClient, inserter).queue()
    }
}
