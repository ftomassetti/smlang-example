package me.tomassetti.smlang

import me.tomassetti.smlang.ast.*
import java.util.*

fun StateMachine.stateByName(name: String) = this.states.find { it.name.equals(name) }!!
fun StateMachine.eventByName(name: String) = this.events.find { it.name.equals(name) }!!
fun StateMachine.inputByName(name: String) = this.inputs.find { it.name.equals(name) }!!

class SymbolTable {
    private val values = HashMap<String, Any>()
    fun readByName(name: String) : Any {
        if (!values.containsKey(name)) {
            throw RuntimeException("Unknown symbol $name")
        }
        return values[name]!!
    }
    fun writeByName(name: String, value: Any) {
        values[name] = value
    }
}

class Interpreter(val stateMachine: StateMachine, val inputsValues: Map<InputDeclaration, Any>) {
    var currentState : StateDeclaration = stateMachine.states.find { it.start }!!
    val symbolTable = SymbolTable()

    init {
        executeEntryActions()
        stateMachine.inputs.forEach { symbolTable.writeByName(it.name, inputsValues[it]!!) }
        stateMachine.variables.forEach { symbolTable.writeByName(it.name, it.value.evaluate(symbolTable)) }
    }

    fun receiveEvent(event: EventDeclaration) {
        val transition = currentState.blocks.filterIsInstance(OnEventBlock::class.java).first { it.evenName == event.name }
        if (transition != null) {
            enterState(stateMachine.stateByName(transition.destinationName))
        }
    }

    private fun enterState(enteredState: StateDeclaration) {
        executeExitActions()
        currentState = enteredState
        executeEntryActions()
    }

    private fun executeEntryActions() {
        currentState.blocks.filterIsInstance(OnEntryBlock::class.java).forEach { it.execute(symbolTable) }
    }

    private fun executeExitActions() {
        currentState.blocks.filterIsInstance(OnExitBlock::class.java).forEach { it.execute(symbolTable) }
    }

}

private fun OnEntryBlock.execute(symbolTable: SymbolTable) {
    this.statements.forEach { it.execute(symbolTable) }
}

private fun  Statement.execute(symbolTable: SymbolTable) {
    when (this) {
        is Print -> println(this.value.evaluate(symbolTable))
        is Assignment -> symbolTable.writeByName(this.varName, this.value.evaluate(symbolTable))
        else -> throw UnsupportedOperationException(this.toString())
    }
}

private fun Expression.evaluate(symbolTable: SymbolTable): Any =
    when (this) {
        is VarReference -> symbolTable.readByName(this.varName)
        is SumExpression -> {
            val l = this.left.evaluate(symbolTable)
            val r = this.right.evaluate(symbolTable)
            if (l is Int) {
                l as Int + r as Int
            } else if (l is String) {
                l as String + r.toString()
            } else {
                throw UnsupportedOperationException(this.toString())
            }
        }
        is IntLit -> Integer.parseInt(this.value)
        is StringLit -> this.value
        else -> throw UnsupportedOperationException(this.toString())
    }

private fun OnExitBlock.execute(symbolTable: SymbolTable) {
    this.statements.forEach { it.execute(symbolTable) }
}
