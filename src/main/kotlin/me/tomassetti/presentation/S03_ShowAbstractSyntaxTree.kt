package me.tomassetti.presentation

import me.tomassetti.antlr.model.multilineString
import me.tomassetti.parsing.toParseTree
import me.tomassetti.smlang.parsing.SMLangParserFacade
import me.tomassetti.smlang.parsing.parseCode

fun main(args: Array<String>) {
    val code = readExampleCode()
    println(SMLangParserFacade.parse(code).root!!.multilineString())
}