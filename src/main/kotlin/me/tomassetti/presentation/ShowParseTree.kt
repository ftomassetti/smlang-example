package me.tomassetti.presentation

import me.tomassetti.parsing.toParseTree
import me.tomassetti.smlang.SMParser
import me.tomassetti.smlang.parsing.parseCode
import me.tomassetti.smlang.parsing.parseResource

fun main(args: Array<String>) {
    println(toParseTree(parseCode("""
            statemachine mySm

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
            }"""), SMParser.VOCABULARY).multiLineString())
}