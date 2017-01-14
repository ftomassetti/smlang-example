package me.tomassetti.smlang.desktopeditor

import me.tomassetti.kanvas.Kanvas
import me.tomassetti.kanvas.languageSupportRegistry
import me.tomassetti.smlang.Interpreter
import me.tomassetti.smlang.ast.InputDeclaration
import me.tomassetti.smlang.ast.StateMachine
import me.tomassetti.smlang.ast.VarDeclaration
import me.tomassetti.smlang.parsing.SMLangParserFacade
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.Window
import java.util.*
import javax.swing.*

fun startSimulation(stateMachine : StateMachine, window: Window, inputValues: Map<InputDeclaration, Any>) {
    val interpreter = Interpreter(stateMachine, inputValues)
    val varValueLabels = HashMap<VarDeclaration, JLabel>()
    val currentStateLabel = JLabel("")
    fun update() {
        currentStateLabel.text = interpreter.currentState.name
        stateMachine.variables.forEach {
            varValueLabels[it]!!.text = interpreter.variableValue(it).toString()
        }
    }
    val simulationDialog = object : JDialog(window, "Simulate ${stateMachine.name}") {
        init {
            val layout = GridBagLayout()
            contentPane.layout = layout


            val c = GridBagConstraints()
            c.fill = GridBagConstraints.BOTH
            c.ipadx = 10
            c.ipady = 10
            c.insets = Insets(10, 10, 10, 10)
            c.gridx = 0
            c.gridy = 0
            c.gridwidth = 1
            c.gridheight = 1
            stateMachine.inputs.forEach {
                c.gridx = 0
                contentPane.add(JLabel(it.name), c)
                c.gridx = 1
                contentPane.add(JLabel(inputValues[it].toString()), c)
                c.gridy = c.gridy + 1
            }
            stateMachine.variables.forEach {
                c.gridx = 0
                contentPane.add(JLabel(it.name), c)
                c.gridx = 1
                varValueLabels[it] = JLabel()
                contentPane.add(varValueLabels[it], c)
                c.gridy = c.gridy + 1
            }
            c.gridx = 0
            contentPane.add(JLabel("Current state"), c)
            c.gridx = 1
            contentPane.add(currentStateLabel, c)
            c.gridy = c.gridy + 1

            c.gridx = 0
            c.gridwidth = 2
            stateMachine.events.forEach { event ->
                val triggerButton = JButton(event.name)
                triggerButton.addActionListener {
                    interpreter.receiveEvent(event)
                    update()
                }
                contentPane.add(triggerButton, c)
                c.gridy = c.gridy + 1
            }

            update()
            this.pack()
            this.isResizable = false
            this.isVisible = true
        }
    }
}

fun simulate(stateMachine : StateMachine, window: Window) {
    val inputsDialog = object : JDialog(window, "Simulate ${stateMachine.name}: getting inputs") {
        init {
            if (stateMachine.inputs.isEmpty()) {
                startSimulation(stateMachine, window, emptyMap())

            } else {

                val layout = GridBagLayout()
                contentPane.layout = layout

                val c = GridBagConstraints()
                val spinnersMap = HashMap<InputDeclaration, JSpinner>()
                c.fill = GridBagConstraints.BOTH
                c.ipadx = 10
                c.ipady = 10
                c.insets = Insets(10, 10, 10, 10)
                c.gridx = 0
                c.gridy = 0
                c.gridwidth = 1
                c.gridheight = 1
                stateMachine.inputs.forEach {
                    c.gridx = 0
                    contentPane.add(JLabel(it.name), c)
                    c.gridx = 1
                    val spinner = JSpinner()
                    spinnersMap[it] = spinner
                    contentPane.add(spinner, c)
                    c.gridy = c.gridy + 1
                }
                c.gridx = 0
                val submit = JButton("Submit")
                contentPane.add(submit, c)
                submit.addActionListener {
                    val inputValues = HashMap<InputDeclaration, Any>()
                    spinnersMap.forEach { s, jSpinner -> inputValues[s] = jSpinner.value }
                    this.isVisible = false
                    startSimulation(stateMachine, window, inputValues)
                }

                this.pack()
                this.isResizable = false
                this.isVisible = true
            }
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
