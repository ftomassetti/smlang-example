package me.tomassetti.smlang.desktopeditor

import org.antlr.v4.runtime.Vocabulary
import org.antlr.v4.runtime.atn.ATN
import org.fife.ui.rsyntaxtextarea.SyntaxScheme
import java.io.File
import java.util.*

data class ParserData(val ruleNames: Array<String>, val vocabulary: Vocabulary, val atn: ATN)

interface LanguageSupport {
    val syntaxScheme : SyntaxScheme
    val antlrLexerFactory: AntlrLexerFactory
    val parserData: ParserData?
}

object languageSupportRegistry {
    private val extensionsMap = HashMap<String, LanguageSupport>()

    fun register(extension : String, languageSupport: LanguageSupport) {
        extensionsMap[extension] = languageSupport
    }
    fun languageSupportForExtension(extension : String) : LanguageSupport {
        if (extensionsMap.containsKey(extension)) {
            return extensionsMap[extension] as LanguageSupport
        } else {
            throw RuntimeException()
        }
    }
    fun languageSupportForFile(file : File) : LanguageSupport = languageSupportForExtension(file.extension)
}