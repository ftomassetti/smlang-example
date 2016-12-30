package me.tomassetti.smlang.desktopeditor

import me.tomassetti.kanvas.AntlrLexerFactory
import me.tomassetti.kanvas.LanguageSupport
import me.tomassetti.kanvas.ParserData
import me.tomassetti.kanvas.PropositionProvider
import me.tomassetti.smlang.SMLexer
import me.tomassetti.smlang.SMParser
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.Token
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
            // Types
            SMLexer.STRING, SMLexer.INT, SMLexer.DECIMAL -> Color(42, 53, 114)

            // Literals
            SMLexer.STRINGLIT -> Color(21, 175, 36)
            SMLexer.INTLIT, SMLexer.DECLIT -> Color.BLUE

            // Comments
            SMLexer.COMMENT -> Color(170, 181, 171)

            // Operators
            SMLexer.ASTERISK, SMLexer.DIVISION, SMLexer.PLUS, SMLexer.MINUS -> Color.WHITE

            // Keywords
            SMLexer.VAR -> Color.GREEN
            SMLexer.INPUT -> Color(200, 250, 200)
            SMLexer.SM -> Color(200, 250, 200)
            SMLexer.EVENT -> Color(200, 250, 200)
            SMLexer.AS -> Color(50, 12, 96)

            // Identifiers
            SMLexer.ID -> Color.MAGENTA

            // Separators
            SMLexer.ARROW -> Color(50, 12, 96)
            SMLexer.COLON -> Color(50, 12, 96)
            SMLexer.ASSIGN -> Color(50, 12, 96)
            SMLexer.LPAREN, SMLexer.RPAREN -> Color.WHITE

            // Rest
            SMLexer.UNMATCHED -> Color.RED
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
            override fun fromTokenType(completionProvider: CompletionProvider,
                                       preecedingTokens: List<Token>, tokenType: Int): List<Completion> {
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
                            println("PRE $preecedingTokens")
                            val determiningToken = preecedingTokens.findLast { setOf(SMLexer.SM, SMLexer.VAR, SMLexer.EVENT, SMLexer.INPUT).contains(it.type) }
                            val text = when (determiningToken?.type) {
                                SMLexer.SM -> "aStateMachine"
                                SMLexer.EVENT -> "anEvent"
                                SMLexer.INPUT -> "aInput"
                                SMLexer.VAR -> "aVar"
                                else -> "someID"
                            }
                            res.add(BasicCompletion(completionProvider, text))
                        }
                    }
                }
                return res
            }
        }
}
