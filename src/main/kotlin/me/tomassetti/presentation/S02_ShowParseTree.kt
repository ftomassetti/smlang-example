package me.tomassetti.presentation

import me.tomassetti.parsing.toParseTree
import me.tomassetti.smlang.SMParser
import me.tomassetti.smlang.parsing.parseCode
import me.tomassetti.smlang.parsing.parseResource

fun main(args: Array<String>) {
    println(toParseTree(parseCode(readExampleCode()), SMParser.VOCABULARY).multiLineString())
}