package me.tomassetti.presentation

import me.tomassetti.smlang.SMLexer
import me.tomassetti.smlang.parsing.lexerForCode
import org.antlr.v4.runtime.Token



fun main(args: Array<String>) {
//    val code = """
//            |statemachine mySm
//            |
//            |input lowSpeedThroughtput: Int
//            |input highSpeedThroughtput: Int
//            |
//            |var totalProduction = 0
//            |
//            |event turnOff
//            |event turnOn
//            |event speedUp
//            |event speedDown
//            |event emergencyStop
//            |event doNothing
//            |
//            |start state turnedOff {
//            |    on turnOn -> turnedOn
//            |}""".trimMargin("|")
    val lexer = lexerForCode(readExampleCode())
    var token : Token? = null
    do {
        token = lexer.nextToken()
        val typeName = SMLexer.VOCABULARY.getSymbolicName(token.type)
        val text = token.text.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")
        println("L${token.line}(${token.startIndex}-${token.stopIndex}) $typeName '$text'")
    } while (token?.type != -1)
}