package de.tobihxd.autocomment;

import com.intellij.psi.PsiClass
import com.intellij.psi.util.PsiTreeUtil

class MethodCollector {

    fun collectAllMethods(psiFile: com.intellij.psi.PsiFile): List<com.intellij.psi.PsiMethod> {
        val classes = PsiTreeUtil.findChildrenOfType(psiFile, PsiClass::class.java)
        return classes.flatMap { it.methods.toList() }
    }
}
