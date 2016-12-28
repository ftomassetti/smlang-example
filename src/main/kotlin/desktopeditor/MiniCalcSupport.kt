package me.tomassetti.minicalc.desktopeditor

import me.tomassetti.minicalc.MiniCalcLexer
import org.antlr.v4.runtime.Lexer
import org.fife.ui.rsyntaxtextarea.Style
import org.fife.ui.rsyntaxtextarea.SyntaxScheme
import java.awt.Color

object sandySyntaxScheme : SyntaxScheme(true) {
    override fun getStyle(index: Int): Style {
        val style = Style()
        val color = when (index) {
            MiniCalcLexer.VAR -> Color.GREEN
            MiniCalcLexer.INPUT -> Color(200, 250, 200)
            MiniCalcLexer.ASSIGN -> Color.GREEN
            MiniCalcLexer.ASTERISK, MiniCalcLexer.DIVISION, MiniCalcLexer.PLUS, MiniCalcLexer.MINUS -> Color.WHITE
            MiniCalcLexer.INTLIT, MiniCalcLexer.DECLIT -> Color.BLUE
            MiniCalcLexer.UNMATCHED -> Color.RED
            MiniCalcLexer.ID -> Color.MAGENTA
            MiniCalcLexer.LPAREN, MiniCalcLexer.RPAREN -> Color.WHITE
            else -> null
        }
        if (color != null) {
            style.foreground = color
        }
        return style
    }
}

object miniCalcSupport : LanguageSupport {
    override val syntaxScheme: SyntaxScheme
        get() = sandySyntaxScheme
    override val antlrLexerFactory: AntlrLexerFactory
        get() = object : AntlrLexerFactory {
            override fun create(code: String): Lexer = MiniCalcLexer(org.antlr.v4.runtime.ANTLRInputStream(code))
        }
    override val parserData: ParserData?
        get() = ParserData(MiniCalcLexer.ruleNames, MiniCalcLexer.VOCABULARY, MiniCalcLexer._ATN)
}
