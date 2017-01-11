package me.tomassetti.smlang.ast

import me.tomassetti.antlr.model.Named
import me.tomassetti.antlr.model.Node
import me.tomassetti.antlr.model.Position
import me.tomassetti.antlr.model.ReferenceByName

//
// StateMachine
//

data class StateMachine(val name: String,
                        val inputs: List<InputDeclaration>,
                        val variables: List<VarDeclaration>,
                        val events: List<EventDeclaration>,
                        val states: List<StateDeclaration>,
                        override val position: Position? = null) : Node

//
// Top level elements
//

data class InputDeclaration(override val name: String, val type: Type, override val position: Position? = null) : Node, Named
data class VarDeclaration(override val name: String, val type: Type?, val value: Expression, override val position: Position? = null) : Node, Named
data class EventDeclaration(override val name: String, override val position: Position? = null) : Node, Named
data class StateDeclaration(override val name: String, val start: Boolean, val blocks: List<StateBlock>, override val position: Position? = null) : Node, Named

//
// Interfaces
//

interface StateBlock : Node { }
interface Statement : Node { }
interface Expression : Node { }
interface Type : Node { }

//
// StateBlocks
//

data class OnEntryBlock(val statements: List<Statement>, override val position: Position? = null) : StateBlock
data class OnExitBlock(val statements: List<Statement>, override val position: Position? = null) : StateBlock
data class OnEventBlock(val event: ReferenceByName<EventDeclaration>,
                        val destination: ReferenceByName<StateDeclaration>,
                        override val position: Position? = null) : StateBlock

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

data class VarReference(val variable: ReferenceByName<VarDeclaration>,
                        override val position: Position? = null) : Expression

data class IntLit(val value: String, override val position: Position? = null) : Expression

data class DecLit(val value: String, override val position: Position? = null) : Expression

data class StringLit(val value: String, override val position: Position? = null) : Expression

//
// Statements
//

data class Assignment(val variable: ReferenceByName<VarDeclaration>, val value: Expression,
                      override val position: Position? = null) : Statement

data class Print(val value: Expression, override val position: Position? = null) : Statement
