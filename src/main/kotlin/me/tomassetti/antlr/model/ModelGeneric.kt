package me.tomassetti.antlr.model

import java.lang.reflect.ParameterizedType
import java.util.*
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType
import kotlin.reflect.memberProperties
import kotlin.reflect.primaryConstructor

//
// Generic part: valid for all languages
//

interface Node {
    val position: Position?
}

const val indentBlock = "  "

fun Node.multilineString(indent: String = "") : String {
    val sb = StringBuffer()
    sb.append("$indent${this.javaClass.simpleName} {\n")
    this.javaClass.kotlin.memberProperties.filter { !it.name.startsWith("component") && !it.name.equals("position") }.forEach {
        val mt = it.returnType.javaType
        if (mt is ParameterizedType && mt.rawType.equals(List::class.java)){
            val paramType = mt.actualTypeArguments[0]
            if (paramType is Class<*> && Node::class.java.isAssignableFrom(paramType)) {
                sb.append("$indent$indentBlock${it.name} = [\n")
                (it.get(this) as List<out Node>).forEach { sb.append(it.multilineString(indent + indentBlock + indentBlock)) }
                sb.append("$indent$indentBlock]\n")
            }
        } else {
            val value = it.get(this)
            if (value is Node) {
                sb.append("$indent$indentBlock${it.name} = [\n")
                sb.append(value.multilineString(indent + indentBlock + indentBlock))
                sb.append("$indent$indentBlock]\n")
            } else {
                sb.append("$indent$indentBlock${it.name} = ${it.get(this)}\n")
            }
        }
    }
    sb.append("$indent}\n")
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

data class ReferenceByName<N : Node>(val name: String, var referred: N? = null)
