package me.tomassetti.smlang.desktopeditor

import me.tomassetti.smlang.SMLexer
import org.antlr.v4.runtime.Lexer
import org.fife.ui.rsyntaxtextarea.Style
import org.fife.ui.rsyntaxtextarea.SyntaxScheme
import java.awt.Color

object sandySyntaxScheme : SyntaxScheme(true) {
    override fun getStyle(index: Int): Style {
        val style = Style()
        val color = when (index) {
            SMLexer.VAR -> Color.GREEN
            SMLexer.INPUT -> Color(200, 250, 200)
            SMLexer.ASSIGN -> Color.GREEN
            SMLexer.ASTERISK, SMLexer.DIVISION, SMLexer.PLUS, SMLexer.MINUS -> Color.WHITE
            SMLexer.INTLIT, SMLexer.DECLIT -> Color.BLUE
            SMLexer.UNMATCHED -> Color.RED
            SMLexer.ID -> Color.MAGENTA
            SMLexer.LPAREN, SMLexer.RPAREN -> Color.WHITE
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
            override fun create(code: String): Lexer = SMLexer(org.antlr.v4.runtime.ANTLRInputStream(code))
        }
    override val parserData: ParserData?
        get() = ParserData(SMLexer.ruleNames, SMLexer.VOCABULARY, SMLexer._ATN)
}
