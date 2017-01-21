package me.tomassetti.smlang.ast

fun BinaryExpression.onNumbers() = (left.type() is NumberType) && (right.type() is NumberType)

fun Expression.type() : Type =
    when (this) {
        is IntLit -> IntType()
        is DecLit -> DecimalType()
        is StringLit -> StringType()
        is SumExpression -> {
            if (this.left.type() is StringType) {
                StringType()
            } else if (onNumbers()) {
                if (left.type() is DecimalType || right.type() is NumberType)
                    DecimalType() else IntType()
            } else {
                throw IllegalArgumentException("This operation should be performed on numbers or start with a string")
            }
        }
        is SubtractionExpression, is MultiplicationExpression, is DivisionExpression -> {
            val be = this as BinaryExpression
            if (!be.onNumbers()) {
                throw IllegalArgumentException("This operation should be performed on numbers")
            }
            if (be.left.type() is DecimalType || be.right.type() is NumberType)
                DecimalType() else IntType()
        }
        is UnaryMinusExpression -> this.value.type()
        is TypeConversion -> this.targetType
        is ValueReference -> this.symbol.referred!!.type
        else -> throw UnsupportedOperationException("No way to calculate the type of $this")
    }
