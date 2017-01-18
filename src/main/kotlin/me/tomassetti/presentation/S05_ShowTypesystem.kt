package me.tomassetti.presentation

import me.tomassetti.antlr.model.specificProcess
import me.tomassetti.smlang.ast.Expression
import me.tomassetti.smlang.ast.type
import me.tomassetti.smlang.parsing.SMLangParserFacade

fun main(args: Array<String>) {
    val code = readExampleCode()
    val parsingResult = SMLangParserFacade.parse(code, withValidation = true)
    parsingResult.root!!.specificProcess(Expression::class.java) {
        println("${it.type().javaClass.simpleName} : ${it.position!!.text(code)}")
    }
}