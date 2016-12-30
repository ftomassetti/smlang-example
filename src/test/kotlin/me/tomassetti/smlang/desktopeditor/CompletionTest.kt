package me.tomassetti.smlang.desktopeditor

import me.tomassetti.kanvas.AntlrAutoCompletionSuggester
import me.tomassetti.kanvas.EditorContextImpl
import me.tomassetti.kanvas.TokenTypeImpl
import me.tomassetti.smlang.SMLexer.*
import me.tomassetti.smlang.SMParser
import me.tomassetti.smlang.desktopeditor.smLangSupport
import kotlin.test.assertEquals
import org.junit.Test as test

class CompletionTest {

    fun suggestions(code: String) = AntlrAutoCompletionSuggester(SMParser.ruleNames,SMParser.VOCABULARY,SMParser._ATN)
            .suggestions(EditorContextImpl(code, smLangSupport.antlrLexerFactory))

    @test fun emptyFile() {
        val code = ""
        assertEquals(setOf(TokenTypeImpl(SM)), suggestions(code))
    }

    @test fun afterSmKeyword() {
        val code = "statemachine "
        assertEquals(setOf(TokenTypeImpl(ID)), suggestions(code))
    }

    @test fun afterSmName() {
        val code = "statemachine foo "
        assertEquals(setOf(TokenTypeImpl(EVENT), TokenTypeImpl(INPUT), TokenTypeImpl(VAR), TokenTypeImpl(START), TokenTypeImpl(STATE)), suggestions(code))
    }

    @test fun afterEventKeyword() {
        val code = "statemachine foo event "
        assertEquals(setOf(TokenTypeImpl(ID)), suggestions(code))
    }

    @test fun afterEventDefinition() {
        val code = "statemachine foo event myEvent "
        assertEquals(setOf(TokenTypeImpl(EVENT), TokenTypeImpl(INPUT), TokenTypeImpl(VAR), TokenTypeImpl(START), TokenTypeImpl(STATE)), suggestions(code))
    }

    @test fun afterVarKeyword() {
        val code = "statemachine foo var "
        assertEquals(setOf(TokenTypeImpl(ID)), suggestions(code))
    }

//    @test fun afterVarDefinition() {
//        val code = "statemachine foo var myVar = 10 "
//        assertEquals(setOf(TokenTypeImpl(EVENT), TokenTypeImpl(INPUT), TokenTypeImpl(VAR), TokenTypeImpl(START), TokenTypeImpl(STATE)), suggestions(code))
//    }

}
