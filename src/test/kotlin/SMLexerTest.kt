package me.tomassetti.smlang

import lexerForCode
import tokensNames
import kotlin.test.assertEquals
import org.junit.Test as test

class SMLexerTest {

    @org.junit.Test fun parseVarDeclarationAssignedAnIntegerLiteral() {
        assertEquals(listOf("VAR", "ID", "ASSIGN", "INTLIT", "EOF"),
                tokensNames(lexerForCode("var a = 1")))
    }

    @org.junit.Test fun parseVarDeclarationAssignedADecimalLiteral() {
        assertEquals(listOf("VAR", "ID", "ASSIGN", "DECLIT", "EOF"),
                tokensNames(lexerForCode("var a = 1.23")))
    }

    @org.junit.Test fun parseVarDeclarationAssignedASum() {
        assertEquals(listOf("VAR", "ID", "ASSIGN", "INTLIT", "PLUS", "INTLIT", "EOF"),
                tokensNames(lexerForCode("var a = 1 + 2")))
    }

    @org.junit.Test fun parseMathematicalExpression() {
        assertEquals(listOf("INTLIT", "PLUS", "ID", "ASTERISK", "INTLIT", "DIVISION", "INTLIT", "MINUS", "INTLIT", "EOF"),
                tokensNames(lexerForCode("1 + a * 3 / 4 - 5")))
    }

    @org.junit.Test fun parseMathematicalExpressionWithParenthesis() {
        assertEquals(listOf("INTLIT", "PLUS", "LPAREN", "ID", "ASTERISK", "INTLIT", "RPAREN", "MINUS", "DECLIT", "EOF"),
                tokensNames(lexerForCode("1 + (a * 3) - 5.12")))
    }

    @org.junit.Test fun parseCast() {
        assertEquals(listOf("ID", "ASSIGN", "ID", "AS", "INT", "EOF"),
                tokensNames(lexerForCode("a = b as Int")))
    }

    @org.junit.Test fun parseSimpleString() {
        assertEquals(listOf("STRINGLIT", "EOF"),
                tokensNames(lexerForCode("\"hi!\"")))
    }

}