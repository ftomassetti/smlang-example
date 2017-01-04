package me.tomassetti.smlang.parsing

import me.tomassetti.smlang.SMLexer
import me.tomassetti.smlang.SMParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.StringReader

fun lexerForCode(code: String) = SMLexer(ANTLRInputStream(StringReader(code)))

fun lexerForResource(resourceName: String, clazz: Class<Any>): SMLexer {
    val inputStream = clazz.getResourceAsStream("/${resourceName}.sm") ?: throw NullPointerException()
    return SMLexer(ANTLRInputStream(inputStream))
}

fun parse(lexer: SMLexer) : SMParser.StateMachineContext = SMParser(CommonTokenStream(lexer)).stateMachine()

fun parseCode(code: String) : SMParser.StateMachineContext = SMParser(CommonTokenStream(lexerForCode(code))).stateMachine()

fun parseResource(resourceName: String, clazz: Class<Any>) : SMParser.StateMachineContext = SMParser(CommonTokenStream(lexerForResource(resourceName, clazz))).stateMachine()