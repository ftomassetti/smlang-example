package me.tomassetti.smlang.desktopeditor

import me.tomassetti.kanvas.Kanvas
import me.tomassetti.kanvas.languageSupportRegistry
import me.tomassetti.smlang.desktopeditor.smLangSupport
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    languageSupportRegistry.register("sm", smLangSupport)
    val kanvas = Kanvas()
    SwingUtilities.invokeLater {
        kanvas.createAndShowKanvasGUI()
        kanvas.addTab("My SM", languageSupport = smLangSupport)
    }

}
