package me.tomassetti.antlr.model

import java.lang.reflect.ParameterizedType
import java.util.*
import kotlin.reflect.KParameter
import kotlin.reflect.memberProperties
import kotlin.reflect.primaryConstructor

//
// Generic part: valid for all languages
//

interface Node {
    val position: Position?
}

fun Node.multilineString(indent: String = "") : String {
    val sb = StringBuffer()
    sb.append("${indent}${this.javaClass.simpleName}{\n")
    println("___$this")
    this.javaClass.kotlin.members.forEach {
        println(it.returnType)
    }
    this.javaClass.methods.forEach {
        if (it.returnType.equals(List::class.java)) {
            val paramType = (it.genericReturnType as ParameterizedType).actualTypeArguments[0]
            if (paramType is Class<*> && Node::class.java.isAssignableFrom(paramType)) {
                //println(" L " + paramType)
            }
        }
        //println(it.returnType.toString() + " "+it+ " "+it.returnType.equals(List::class.java)) }
    }
    sb.append("${indent}}\n")
    return sb.toString()
}

fun Node.isBefore(other: Node) : Boolean = position!!.start.isBefore(other.position!!.start)

fun Point.isBefore(other: Point) : Boolean = line < other.line || (line == other.line && column < other.column)

data class Point(val line: Int, val column: Int) {
    override fun toString() = "Line $line, Column $column"
}

fun Point.offset(code: String) : Int {
    val lines = code.split("\n")
    return lines.subList(0, this.line - 1).foldRight(0, { it, acc -> it.length + acc }) + column
}

data class Position(val start: Point, val end: Point)

fun Position.length(code: String) = end.offset(code) - start.offset(code)

fun pos(startLine:Int, startCol:Int, endLine:Int, endCol:Int) = Position(Point(startLine,startCol),Point(endLine,endCol))

fun Node.process(operation: (Node) -> Unit) {
    operation(this)
    this.javaClass.kotlin.memberProperties.forEach { p ->
        val v = p.get(this)
        when (v) {
            is Node -> v.process(operation)
            is Collection<*> -> v.forEach { if (it is Node) it.process(operation) }
        }
    }
}

fun <T: Node> Node.specificProcess(klass: Class<T>, operation: (T) -> Unit) {
    process { if (klass.isInstance(it)) { operation(it as T) } }
}

fun Node.transform(operation: (Node) -> Node) : Node {
    operation(this)
    val changes = HashMap<String, Any>()
    this.javaClass.kotlin.memberProperties.forEach { p ->
        val v = p.get(this)
        when (v) {
            is Node -> {
                val newValue = v.transform(operation)
                if (newValue != v) changes[p.name] = newValue
            }
            is Collection<*> -> {
                val newValue = v.map { if (it is Node) it.transform(operation) else it }
                if (newValue != v) changes[p.name] = newValue
            }
        }
    }
    var instanceToTransform = this
    if (!changes.isEmpty()) {
        val constructor = this.javaClass.kotlin.primaryConstructor!!
        val params = HashMap<KParameter, Any?>()
        constructor.parameters.forEach { param ->
            if (changes.containsKey(param.name)) {
                params[param] = changes[param.name]
            } else {
                params[param] = this.javaClass.kotlin.memberProperties.find { param.name == it.name }!!.get(this)
            }
        }
        instanceToTransform = constructor.callBy(params)
    }
    return operation(instanceToTransform)
}

