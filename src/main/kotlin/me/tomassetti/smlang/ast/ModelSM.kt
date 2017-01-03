package me.tomassetti.smlang.ast

import me.tomassetti.antlr.model.Node
import me.tomassetti.antlr.model.Position
import java.util.*
import kotlin.reflect.KParameter
import kotlin.reflect.memberProperties
import kotlin.reflect.primaryConstructor

//
// SMLang specific part
//

data class StateMachine(val inputs : List<InputDeclaration>, val variables : List<VarDeclaration>,  override val position: Position? = null) : Node

//
// Preamble
//

data class InputDeclaration(val inputName: String, val type: Type, override val position: Position? = null) : Node
data class VarDeclaration(val varName: String, val value: Expression, override val position: Position? = null) : Node

//
//
//

interface Statement : Node { }

interface Expression : Node { }

interface Type : Node { }

//
// Types
//

data class IntType(override val position: Position? = null) : Type

data class DecimalType(override val position: Position? = null) : Type

//
// Expressions
//

interface BinaryExpression : Expression {
    val left: Expression
    val right: Expression
}

data class SumExpression(override val left: Expression, override val right: Expression, override val position: Position? = null) : BinaryExpression

data class SubtractionExpression(override val left: Expression, override val right: Expression, override val position: Position? = null) : BinaryExpression

data class MultiplicationExpression(override val left: Expression, override val right: Expression, override val position: Position? = null) : BinaryExpression

data class DivisionExpression(override val left: Expression, override val right: Expression, override val position: Position? = null) : BinaryExpression

data class UnaryMinusExpression(val value: Expression, override val position: Position? = null) : Expression

data class TypeConversion(val value: Expression, val targetType: Type, override val position: Position? = null) : Expression

data class VarReference(val varName: String, override val position: Position? = null) : Expression

data class IntLit(val value: String, override val position: Position? = null) : Expression

data class DecLit(val value: String, override val position: Position? = null) : Expression

//
// Statements
//

data class Assignment(val varName: String, val value: Expression, override val position: Position? = null) : Statement

data class Print(val value: Expression, override val position: Position? = null) : Statement
