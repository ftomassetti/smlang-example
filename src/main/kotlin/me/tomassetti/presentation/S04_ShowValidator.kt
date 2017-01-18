package me.tomassetti.presentation

import me.tomassetti.antlr.model.multilineString
import me.tomassetti.smlang.parsing.SMLangParserFacade

fun main(args: Array<String>) {
    val code = readExampleCode()
    val parsingResult = SMLangParserFacade.parse(code, withValidation = true)
    if (parsingResult.root == null) {
        println("Syntax error: I cannot build the AST")
    } else {
        println(parsingResult.root!!.multilineString())
    }
    println("=== Validation: total errors ${parsingResult.errors.size} ===")
    parsingResult.errors.forEach { println("at ${it.position} ${it.message}") }
}