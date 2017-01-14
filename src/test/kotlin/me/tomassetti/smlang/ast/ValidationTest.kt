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
        assertEquals("An assignment to symbol 'a' cannot be resolved", res.errors[0].message)
    }

    @test fun unresolvedVarReference() {
        val code = """statemachine basic
                      |var a = 0
                      |start state nothing { on entry { a = b } }""".trimMargin("|")
        val res = SMLangParserFacade.parse(code)
        assertEquals(1, res.errors.size)
        assertEquals("A reference to symbol 'b' cannot be resolved", res.errors[0].message)
    }

    @test fun unresolvedStateReference() {
        val code = """statemachine basic
                      |event foo
                      |start state nothing { on foo -> b }""".trimMargin("|")
        val res = SMLangParserFacade.parse(code)
        assertEquals(1, res.errors.size)
        assertEquals("A reference to state 'b' cannot be resolved", res.errors[0].message)
    }

    @test fun unresolvedEventReference() {
        val code = """statemachine basic
                      |start state nothing { on foo -> b }
                      |state b {}""".trimMargin("|")
        val res = SMLangParserFacade.parse(code)
        assertEquals(1, res.errors.size)
        assertEquals("A reference to event 'foo' cannot be resolved", res.errors[0].message)
    }

}