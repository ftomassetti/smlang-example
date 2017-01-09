package me.tomassetti.smlang.parsing

import me.tomassetti.antlr.model.Point
import me.tomassetti.antlr.model.Position
import kotlin.test.assertEquals
import org.junit.Test as test

class ParsingErrorsTest {

    @org.junit.Test fun missingInputType() {
        val res = SMLangParserFacade.parse("statemachine sm\n input foo")
        assertEquals(1, res.errors.size)
        assertEquals("mismatched input '<EOF>' expecting ':'", res.errors[0].message)
        assertEquals(Position(Point(2, 10), Point(2, 11)), res.errors[0].position)
    }

}