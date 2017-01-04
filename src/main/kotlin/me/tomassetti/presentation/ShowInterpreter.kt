package me.tomassetti.presentation

import me.tomassetti.smlang.Interpreter
import me.tomassetti.smlang.eventByName
import me.tomassetti.smlang.inputByName
import me.tomassetti.smlang.parsing.SMLangParserFacade

fun main(args: Array<String>) {
    val parseResult = SMLangParserFacade.parse("""statemachine mySm

input lowSpeedThroughtput: Int
input highSpeedThroughtput: Int

var totalProduction = 0

event turnOff
event turnOn
event speedUp
event speedDown
event emergencyStop
event doNothing

start state turnedOff {
    on turnOn -> turnedOn
}

state turnedOn {
    on turnedOff -> turnedOff
    on speedUp -> lowSpeed
}

state lowSpeed {
    on entry {
        totalProduction = totalProduction + lowSpeedThroughtput
        print("Producing " + lowSpeedThroughtput + " elements (total "+totalProduction+")")
    }
    on speedDown -> turnedOn
    on speedUp -> highSpeed
    on doNothing -> lowSpeed
}

state highSpeed {
    on entry {
        totalProduction = totalProduction + highSpeedThroughtput
        print("Producing " + highSpeedThroughtput + " elements (total "+totalProduction+")")
    }
    on speedDown -> turnedOn
    on emergencyStop -> turnedOn
    on doNothing -> highSpeed
}""")
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