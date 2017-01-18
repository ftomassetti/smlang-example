package me.tomassetti.presentation

import me.tomassetti.smlang.Interpreter
import me.tomassetti.smlang.eventByName
import me.tomassetti.smlang.inputByName
import me.tomassetti.smlang.parsing.SMLangParserFacade

fun main(args: Array<String>) {
    val code = readExampleCode()
    val parseResult = SMLangParserFacade.parse(code, withValidation = true)
    println(parseResult.errors)

    val stateMachine = parseResult.root!!
    val interpreter = Interpreter(stateMachine, mapOf(
            stateMachine.inputByName("lowSpeedThroughtput") to 2,
            stateMachine.inputByName("highSpeedThroughtput") to 5))
    interpreter.receiveEvent(stateMachine.eventByName("turnOn"))
    interpreter.receiveEvent(stateMachine.eventByName("speedUp"))
    interpreter.receiveEvent(stateMachine.eventByName("doNothing"))
    interpreter.receiveEvent(stateMachine.eventByName("doNothing"))
    interpreter.receiveEvent(stateMachine.eventByName("speedUp"))
    interpreter.receiveEvent(stateMachine.eventByName("doNothing"))
    interpreter.receiveEvent(stateMachine.eventByName("emergencyStop"))
}