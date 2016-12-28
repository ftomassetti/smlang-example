package me.tomassetti.smlang

import parseResource
import kotlin.test.assertEquals
import org.junit.Test as test

class SMParserTest {


    @org.junit.Test fun parseAdditionAssignment() {
        assertEquals(
"""MiniCalcFile
  Line
    AssignmentStatement
      Assignment
        T[a]
        T[=]
        BinaryOperation
          IntLiteral
            T[1]
          T[+]
          IntLiteral
            T[2]
    T[<EOF>]
""",
                toParseTree(parseResource("addition_assignment", this.javaClass)).multiLineString())
    }

    @org.junit.Test fun parseSimplestVarDecl() {
        assertEquals(
"""MiniCalcFile
  Line
    VarDeclarationStatement
      VarDeclaration
        T[var]
        Assignment
          T[a]
          T[=]
          IntLiteral
            T[1]
    T[<EOF>]
""",
                toParseTree(parseResource("simplest_var_decl", this.javaClass)).multiLineString())
    }

    @org.junit.Test fun parsePrecedenceExpressions() {
        assertEquals(
"""MiniCalcFile
  Line
    VarDeclarationStatement
      VarDeclaration
        T[var]
        Assignment
          T[a]
          T[=]
          BinaryOperation
            BinaryOperation
              IntLiteral
                T[1]
              T[+]
              BinaryOperation
                IntLiteral
                  T[2]
                T[*]
                IntLiteral
                  T[3]
            T[-]
            IntLiteral
              T[4]
    T[<EOF>]
""",
                toParseTree(parseResource("precedence_expression", this.javaClass)).multiLineString())
    }

}