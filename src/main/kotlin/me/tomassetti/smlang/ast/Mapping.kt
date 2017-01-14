package me.tomassetti.smlang.ast

import me.tomassetti.antlr.model.ParseTreeToAstMapper
import me.tomassetti.antlr.model.ReferenceByName
import me.tomassetti.antlr.model.toPosition
import me.tomassetti.smlang.SMParser.*

class SMParseTreeToAstMapper : ParseTreeToAstMapper<StateMachineContext, StateMachine> {
    override fun map(parseTreeNode: StateMachineContext): StateMachine = parseTreeNode.toAst()
}

//
// StateMachine
//

fun StateMachineContext.toAst(considerPosition: Boolean = false) : StateMachine = StateMachine(
        this.preamble().name.text,
        this.preamble().elements.filterIsInstance(InputDeclContext::class.java) .map { it.toAst(considerPosition) },
        this.preamble().elements.filterIsInstance(VarDeclContext::class.java) .map { it.toAst(considerPosition) },
        this.preamble().elements.filterIsInstance(EventDeclContext::class.java) .map { it.toAst(considerPosition) },
        this.states.map { it.toAst(considerPosition) },
        toPosition(considerPosition))

//
// Top level elements
//

fun InputDeclContext.toAst(considerPosition: Boolean = false) : InputDeclaration = InputDeclaration(
        this.name.text, this.type().toAst(considerPosition), toPosition(considerPosition))

fun VarDeclContext.toAst(considerPosition: Boolean = false) : VarDeclaration = VarDeclaration(
        this.name.text, this.type()?.toAst(considerPosition), this.initialValue.toAst(considerPosition), toPosition(considerPosition))

fun EventDeclContext.toAst(considerPosition: Boolean = false) : EventDeclaration = EventDeclaration(
        this.name.text, toPosition(considerPosition) )

fun StateContext.toAst(considerPosition: Boolean = false) : StateDeclaration = StateDeclaration(
        this.name.text, this.start != null, this.blocks.map { it.toAst(considerPosition) }, toPosition(considerPosition))

//
// StateBlocks
//

fun StateBlockContext.toAst(considerPosition: Boolean = false) : StateBlock = when (this) {
    is EntryBlockContext -> OnEntryBlock(this.statements.map { it.toAst(considerPosition) })
    is ExitBlockContext -> OnExitBlock(this.statements.map { it.toAst(considerPosition) })
    is TransitionBlockContext -> OnEventBlock(ReferenceByName(this.eventName.text),
            ReferenceByName(this.destinationName.text), toPosition(considerPosition))
    else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

//
// Types
//

fun TypeContext.toAst(considerPosition: Boolean = false) : Type = when (this) {
    is IntegerContext -> IntType(toPosition(considerPosition))
    is DecimalContext -> DecimalType(toPosition(considerPosition))
    else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

//
// Expressions
//

fun ExpressionContext.toAst(considerPosition: Boolean = false) : Expression = when (this) {
    is BinaryOperationContext -> toAst(considerPosition)
    is IntLiteralContext -> IntLit(text, toPosition(considerPosition))
    is DecimalLiteralContext -> DecLit(text, toPosition(considerPosition))
    is StringLiteralContext -> StringLit(text, toPosition(considerPosition))
    is ParenExpressionContext -> expression().toAst(considerPosition)
    is VarReferenceContext -> ValueReference(ReferenceByName(text), toPosition(considerPosition))
    is TypeConversionContext -> TypeConversion(expression().toAst(considerPosition), targetType.toAst(considerPosition), toPosition(considerPosition))
    else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun BinaryOperationContext.toAst(considerPosition: Boolean = false) : Expression = when (operator.text) {
    "+" -> SumExpression(left.toAst(considerPosition), right.toAst(considerPosition), toPosition(considerPosition))
    "-" -> SubtractionExpression(left.toAst(considerPosition), right.toAst(considerPosition), toPosition(considerPosition))
    "*" -> MultiplicationExpression(left.toAst(considerPosition), right.toAst(considerPosition), toPosition(considerPosition))
    "/" -> DivisionExpression(left.toAst(considerPosition), right.toAst(considerPosition), toPosition(considerPosition))
    else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

//
// Statements
//

fun StatementContext.toAst(considerPosition: Boolean = false) : Statement = when (this) {
    is AssignmentStatementContext -> Assignment(ReferenceByName(assignment().ID().text),
            assignment().expression().toAst(considerPosition), toPosition(considerPosition))
    is PrintStatementContext -> Print(print().expression().toAst(considerPosition), toPosition(considerPosition))
    else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}
