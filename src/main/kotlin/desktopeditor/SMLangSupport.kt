package me.tomassetti.smlang.desktopeditor

import me.tomassetti.kanvas.AntlrLexerFactory
import me.tomassetti.kanvas.LanguageSupport
import me.tomassetti.kanvas.ParserData
import me.tomassetti.kanvas.PropositionProvider
import me.tomassetti.smlang.SMLexer
import me.tomassetti.smlang.SMParser
import org.antlr.v4.runtime.Lexer
import org.fife.ui.autocomplete.BasicCompletion
import org.fife.ui.autocomplete.Completion
import org.fife.ui.autocomplete.CompletionProvider
import org.fife.ui.rsyntaxtextarea.Style
import org.fife.ui.rsyntaxtextarea.SyntaxScheme
import java.awt.Color
import java.util.*

object smLangSyntaxScheme : SyntaxScheme(true) {
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

object smLangSupport : LanguageSupport {
    override val syntaxScheme: SyntaxScheme
        get() = smLangSyntaxScheme
    override val antlrLexerFactory: AntlrLexerFactory
        get() = object : AntlrLexerFactory {
            override fun create(code: String): Lexer = SMLexer(org.antlr.v4.runtime.ANTLRInputStream(code))
        }
    override val parserData: ParserData?
        get() = ParserData(SMParser.ruleNames, SMParser.VOCABULARY, SMParser._ATN)
    override val propositionProvider: PropositionProvider
        get() = object : PropositionProvider {
            override fun fromTokenType(completionProvider: CompletionProvider, tokenType: Int): List<Completion> {
                val res = LinkedList<Completion>()
                var proposition : String? = this@smLangSupport.parserData!!.vocabulary.getLiteralName(tokenType)
                if (proposition != null) {
                    if (proposition.startsWith("'") && proposition.endsWith("'")) {
                        proposition = proposition.substring(1, proposition.length - 1)
                    }
                    res.add(BasicCompletion(completionProvider, proposition))
                } else {
                    when (tokenType) {
                        SMParser.ID -> {
                            res.add(BasicCompletion(completionProvider, "someID"))
                        }
                    }
                }
                return res
            }
        }
}
