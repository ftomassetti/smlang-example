import me.tomassetti.smlang.SMLexer
import me.tomassetti.smlang.SMParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.StringReader
import java.util.*

fun lexerForCode(code: String) = SMLexer(ANTLRInputStream(StringReader(code)))

fun lexerForResource(resourceName: String, clazz: Class<Any>) = SMLexer(ANTLRInputStream(clazz.getResourceAsStream("/${resourceName}.mc")))

fun tokensNames(lexer: SMLexer): List<String> {
    val tokens = LinkedList<String>()
    do {
        val t = lexer.nextToken()
        when (t.type) {
            -1 -> tokens.add("EOF")
            else -> if (t.type != SMLexer.WS) tokens.add(lexer.vocabulary.getSymbolicName(t.type))
        }
    } while (t.type != -1)
    return tokens
}

fun tokensContent(lexer: SMLexer): List<String> {
    val tokens = LinkedList<String>()
    do {
        val t = lexer.nextToken()
        when (t.type) {
            -1 -> tokens.add("EOF")
            else -> if (t.type != SMLexer.WS) tokens.add(lexer.text)
        }
    } while (t.type != -1)
    return tokens
}

fun parse(lexer: SMLexer) : SMParser.StateMachineContext = SMParser(CommonTokenStream(lexer)).stateMachine()

fun parseResource(resourceName: String, clazz: Class<Any>) : SMParser.StateMachineContext = SMParser(CommonTokenStream(lexerForResource(resourceName, clazz))).stateMachine()
