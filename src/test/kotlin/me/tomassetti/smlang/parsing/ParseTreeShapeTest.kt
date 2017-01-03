package me.tomassetti.smlang.parsing

import kotlin.test.assertEquals
import org.junit.Test as test

class ParseTreeShapeTest {

    @org.junit.Test fun parseBasicSm() {
        assertEquals(
"""StateMachine
  Preamble
    T[statemachine]
    T[basic]
  State
    T[start]
    T[state]
    T[nothing]
    T[{]
    T[}]
""",
                toParseTree(parseResource("basic", this.javaClass)).multiLineString())
    }

    @org.junit.Test fun parseAdditionAssignment() {
        assertEquals(
"""StateMachine
  Preamble
    T[statemachine]
    T[basic]
    VarDecl
      T[var]
      T[a]
      T[=]
      IntLiteral
        T[0]
  State
    T[start]
    T[state]
    T[nothing]
    T[{]
    EntryBlock
      T[on]
      T[entry]
      T[{]
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
      T[}]
    T[}]
""",
                toParseTree(parseResource("addition_assignment", this.javaClass)).multiLineString())
    }

    @org.junit.Test fun parseSimplestVarDecl() {
        assertEquals(
"""StateMachine
  Preamble
    T[statemachine]
    T[basic]
    VarDecl
      T[var]
      T[a]
      T[=]
      IntLiteral
        T[1]
  State
    T[start]
    T[state]
    T[nothing]
    T[{]
    T[}]
""",
                toParseTree(parseResource("simplest_var_decl", this.javaClass)).multiLineString())
    }

    @org.junit.Test fun parsePrecedenceExpressions() {
        assertEquals(
"""StateMachine
  Preamble
    T[statemachine]
    T[basic]
    VarDecl
      T[var]
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
  State
    T[start]
    T[state]
    T[nothing]
    T[{]
    T[}]
""",
                toParseTree(parseResource("precedence_expression", this.javaClass)).multiLineString())
    }

}