package me.tomassetti.presentation

import me.tomassetti.antlr.model.multilineString
import me.tomassetti.parsing.toParseTree
import me.tomassetti.smlang.parsing.SMLangParserFacade
import me.tomassetti.smlang.parsing.parseCode

fun main(args: Array<String>) {
    println(SMLangParserFacade.parse("""
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
            }""").root!!.multilineString())
}