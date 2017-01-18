package me.tomassetti.smlang.ast

fun Expression.type() : Type =
    when (this) {
        is IntLit -> IntType()
        is DecLit -> DecimalType()
        is StringLit -> StringType()
        is SumExpression -> this.left.type()
        is SubtractionExpression -> this.left.type()
        is MultiplicationExpression -> this.left.type()
        is DivisionExpression -> this.left.type()
        is UnaryMinusExpression -> this.value.type()
        is TypeConversion -> this.targetType
        is ValueReference -> this.symbol.referred!!.type
        else -> throw UnsupportedOperationException("No way to calculate the type of $this")
    }