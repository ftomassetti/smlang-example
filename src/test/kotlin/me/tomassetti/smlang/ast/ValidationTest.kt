package me.tomassetti.smlang.ast

import me.tomassetti.smlang.parsing.SMLangParserFacade
import kotlin.test.assertEquals
import org.junit.Test as test

class ValidationTest {

    @test fun simpleOk() {
        val code = """statemachine basic
                      |start state nothing {  }""".trimMargin("|")
        val res = SMLangParserFacade.parse(code)
        assertEquals(0, res.errors.size)
    }

    @test fun unresolvedVarAssignment() {
        val code = """statemachine basic
                      |start state nothing { on entry { a = 1 + 2 } }""".trimMargin("|")
        val res = SMLangParserFacade.parse(code)
        assertEquals(1, res.errors.size)
        assertEquals("An assignment to variable 'a' cannot be resolved", res.errors[0].message)
    }

    @test fun unresolvedVarReference() {
        val code = """statemachine basic
                      |var a = 0
                      |start state nothing { on entry { a = b } }""".trimMargin("|")
        val res = SMLangParserFacade.parse(code)
        assertEquals(1, res.errors.size)
        assertEquals("A reference to variable 'b' cannot be resolved", res.errors[0].message)
    }

}