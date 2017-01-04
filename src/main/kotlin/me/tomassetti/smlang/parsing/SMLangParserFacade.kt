package me.tomassetti.smlang.parsing

import me.tomassetti.antlr.model.*
import me.tomassetti.smlang.SMLexer
import me.tomassetti.smlang.SMParser
import me.tomassetti.smlang.SMParser.*
import me.tomassetti.smlang.ast.*
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

data class AntlrParsingResult(val root : StateMachineContext?, val errors: List<Error>) {
    fun isCorrect() = errors.isEmpty() && root != null
}

data class ParsingResult(val root : StateMachine?, val errors: List<Error>) {
    fun isCorrect() = errors.isEmpty() && root != null
}

fun String.toStream(charset: Charset = Charsets.UTF_8) = ByteArrayInputStream(toByteArray(charset))

object SMLangAntlrParserFacade {

    fun parse(code: String) : AntlrParsingResult = parse(code.toStream())

    fun parse(file: File) : AntlrParsingResult = parse(FileInputStream(file))

    fun parse(inputStream: InputStream) : AntlrParsingResult {
        val lexicalAndSyntaticErrors = LinkedList<Error>()
        val errorListener = object : ANTLRErrorListener {
            override fun reportAmbiguity(p0: Parser?, p1: DFA?, p2: Int, p3: Int, p4: Boolean, p5: BitSet?, p6: ATNConfigSet?) {
                // Ignored for now
            }

            override fun reportAttemptingFullContext(p0: Parser?, p1: DFA?, p2: Int, p3: Int, p4: BitSet?, p5: ATNConfigSet?) {
                // Ignored for now
            }

            override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInline: Int, msg: String, ex: RecognitionException?) {
                println("OFFENDING SYMBOL $offendingSymbol ${offendingSymbol?.javaClass}")
                if (offendingSymbol is CommonToken && offendingSymbol.type != -1) {
                    println("LINE $line")
                    println("charPositionInline $charPositionInline")
                    println("type ${offendingSymbol.type}")
                    println("offendingSymbol.startPoint ${offendingSymbol.startPoint()}")
                    println("offendingSymbol.endPoint ${offendingSymbol.endPoint()}")
                    lexicalAndSyntaticErrors.add(Error(msg, Position(Point(line, charPositionInline), Point(offendingSymbol.endPoint().line, offendingSymbol.endPoint().column))))
                } else {
                    lexicalAndSyntaticErrors.add(Error(msg, Position(Point(line, charPositionInline), Point(line, charPositionInline + 1))))
                }
            }

            override fun reportContextSensitivity(p0: Parser?, p1: DFA?, p2: Int, p3: Int, p4: Int, p5: ATNConfigSet?) {
                // Ignored for now
            }
        }

        val lexer = SMLexer(ANTLRInputStream(inputStream))
        lexer.removeErrorListeners()
        lexer.addErrorListener(errorListener)
        val parser = SMParser(CommonTokenStream(lexer))
        parser.removeErrorListeners()
        parser.addErrorListener(errorListener)
        val antlrRoot = parser.stateMachine()
        return AntlrParsingResult(antlrRoot, lexicalAndSyntaticErrors)
    }

}

object SMLangParserFacade {

    fun parse(code: String) : ParsingResult = parse(code.toStream())

    fun parse(file: File) : ParsingResult = parse(FileInputStream(file))

    fun parse(inputStream: InputStream) : ParsingResult {
        val antlrParsingResult = SMLangAntlrParserFacade.parse(inputStream)
        val lexicalAnsSyntaticErrors = antlrParsingResult.errors
        val antlrRoot = antlrParsingResult.root
        val astRoot = if (lexicalAnsSyntaticErrors.isEmpty()) {antlrRoot?.toAst(considerPosition = true) } else { null }
        println("PARSE ASTROOT=$astRoot")
        val semanticErrors = astRoot?.validate() ?: emptyList()
        return ParsingResult(astRoot, lexicalAnsSyntaticErrors + semanticErrors)
    }

}