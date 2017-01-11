package me.tomassetti.smlang.desktopeditor

import me.tomassetti.kanvas.*
import me.tomassetti.smlang.ast.StateMachine
import me.tomassetti.smlang.desktopeditor.smLangSupport
import me.tomassetti.smlang.parsing.SMLangAntlrParserFacade
import me.tomassetti.smlang.parsing.SMLangParserFacade
import java.awt.GridBagLayout
import java.awt.Window
import javax.swing.*

fun simulate(stateMachine : StateMachine, window: Window) {
    val dialog = object : JDialog(window, "Simulate ${stateMachine.name}") {
        init {
            val layout = GridBagLayout()
            contentPane.layout = layout

            //val inputs

            this.pack()
            this.isVisible = true
        }
    }
}

fun main(args: Array<String>) {
    languageSupportRegistry.register("sm", smLangSupport)
    val kanvas = object : Kanvas() {
        override fun populateMenu(menuBar: JMenuBar) {
            super.populateMenu(menuBar)
            val simulateMenu = JMenu("Simulate")
            menuBar.add(simulateMenu)
            val launch = JMenuItem("Launch")
            launch.addActionListener {
                val code = this.currentCode
                if (code == null) {
                    JOptionPane.showMessageDialog(menuBar, "No code!")
                } else {
                    val res = SMLangParserFacade.parse(code)
                    if (res.isCorrect()) {
                        simulate(res.root!!, menuBar.topLevelAncestor as Window)
                    } else {
                        JOptionPane.showMessageDialog(menuBar, "The code contains errors")
                    }
                }
            }
            simulateMenu.add(launch)
        }
    }
    SwingUtilities.invokeLater {
        kanvas.createAndShowKanvasGUI()
        kanvas.addTab("My SM", languageSupport = smLangSupport)
    }
}
