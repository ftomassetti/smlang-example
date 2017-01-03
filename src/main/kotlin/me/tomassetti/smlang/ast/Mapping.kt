package me.tomassetti.smlang.ast

import me.tomassetti.antlr.model.Node
import me.tomassetti.antlr.model.ParseTreeToAstMapper
import me.tomassetti.antlr.model.Point
import me.tomassetti.antlr.model.Position
import me.tomassetti.smlang.SMParser.*
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token

fun StateMachineContext.toAst(considerPosition: Boolean = false) : StateMachine = StateMachine(
        this.preamble().elements.filter { it is InputDeclContext } .map { (it as InputDeclContext).toAst(considerPosition) },
        this.preamble().elements.filter { it is VarDeclContext } .map { (it as VarDeclContext).toAst(considerPosition) },
        toPosition(considerPosition))

fun Token.startPoint() = Point(line, charPositionInLine)

fun Token.endPoint() = Point(line, charPositionInLine + text.length)

fun ParserRuleContext.toPosition(considerPosition: Boolean) : Position? {
    return if (considerPosition) Position(start.startPoint(), stop.endPoint()) else null
}

fun VarDeclContext.toAst(considerPosition: Boolean = false) : VarDeclaration = VarDeclaration(this.name.text, this.initialValue.toAst(considerPosition), toPosition(considerPosition))
fun InputDeclContext.toAst(considerPosition: Boolean = false) : InputDeclaration = InputDeclaration(this.name.text, this.type().toAst(considerPosition), toPosition(considerPosition))

fun StatementContext.toAst(considerPosition: Boolean = false) : Statement = when (this) {
    is AssignmentStatementContext -> Assignment(assignment().ID().text, assignment().expression().toAst(considerPosition), toPosition(considerPosition))
    is PrintStatementContext -> Print(print().expression().toAst(considerPosition), toPosition(considerPosition))
    else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun ExpressionContext.toAst(considerPosition: Boolean = false) : Expression = when (this) {
    is BinaryOperationContext -> toAst(considerPosition)
    is IntLiteralContext -> IntLit(text, toPosition(considerPosition))
    is DecimalLiteralContext -> DecLit(text, toPosition(considerPosition))
    is ParenExpressionContext -> expression().toAst(considerPosition)
    is VarReferenceContext -> VarReference(text, toPosition(considerPosition))
    is TypeConversionContext -> TypeConversion(expression().toAst(considerPosition), targetType.toAst(considerPosition), toPosition(considerPosition))
    else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun TypeContext.toAst(considerPosition: Boolean = false) : Type = when (this) {
    is IntegerContext -> IntType(toPosition(considerPosition))
    is DecimalContext -> DecimalType(toPosition(considerPosition))
    else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun BinaryOperationContext.toAst(considerPosition: Boolean = false) : Expression = when (operator.text) {
    "+" -> SumExpression(left.toAst(considerPosition), right.toAst(considerPosition), toPosition(considerPosition))
    "-" -> SubtractionExpression(left.toAst(considerPosition), right.toAst(considerPosition), toPosition(considerPosition))
    "*" -> MultiplicationExpression(left.toAst(considerPosition), right.toAst(considerPosition), toPosition(considerPosition))
    "/" -> DivisionExpression(left.toAst(considerPosition), right.toAst(considerPosition), toPosition(considerPosition))
    else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

class SMParseTreeToAstMapper : ParseTreeToAstMapper<StateMachineContext, StateMachine> {
    override fun map(parseTreeNode: StateMachineContext): StateMachine = parseTreeNode.toAst()
}
