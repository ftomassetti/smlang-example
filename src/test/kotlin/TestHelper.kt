import me.tomassetti.minicalc.MiniCalcLexer
import me.tomassetti.minicalc.MiniCalcParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.StringReader
import java.util.*

/**
 * Created by federico on 24/11/16.
 */

fun lexerForCode(code: String) = MiniCalcLexer(ANTLRInputStream(StringReader(code)))

fun lexerForResource(resourceName: String, clazz: Class<Any>) = MiniCalcLexer(ANTLRInputStream(clazz.getResourceAsStream("/${resourceName}.mc")))

fun tokensNames(lexer: MiniCalcLexer): List<String> {
    val tokens = LinkedList<String>()
    do {
        val t = lexer.nextToken()
        when (t.type) {
            -1 -> tokens.add("EOF")
            else -> if (t.type != MiniCalcLexer.WS) tokens.add(lexer.vocabulary.getSymbolicName(t.type))
        }
    } while (t.type != -1)
    return tokens
}

fun tokensContent(lexer: MiniCalcLexer): List<String> {
    val tokens = LinkedList<String>()
    do {
        val t = lexer.nextToken()
        when (t.type) {
            -1 -> tokens.add("EOF")
            else -> if (t.type != MiniCalcLexer.WS) tokens.add(lexer.text)
        }
    } while (t.type != -1)
    return tokens
}

fun parse(lexer: MiniCalcLexer) : MiniCalcParser.MiniCalcFileContext = MiniCalcParser(CommonTokenStream(lexer)).miniCalcFile()

fun parseResource(resourceName: String, clazz: Class<Any>) : MiniCalcParser.MiniCalcFileContext = MiniCalcParser(CommonTokenStream(lexerForResource(resourceName, clazz))).miniCalcFile()
