package me.tomassetti.antlr.model

import org.antlr.v4.runtime.ParserRuleContext

interface ParseTreeToAstMapper<in PTN : ParserRuleContext, out ASTN : Node> {
    fun map(parseTreeNode: PTN) : ASTN
}