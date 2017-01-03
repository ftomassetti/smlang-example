package me.tomassetti.smlang.ast

import me.tomassetti.antlr.model.Position
import me.tomassetti.antlr.model.specificProcess
import java.util.*
import me.tomassetti.antlr.model.Error

fun StateMachine.validate() : List<Error> {
    println("STATEMACHINE VALIDATE")
    val errors = LinkedList<Error>()

    // check a variable or input is not duplicated
    val varsByName = HashMap<String, Int>()
    this.specificProcess(VarDeclaration::class.java) {
        if (varsByName.containsKey(it.name)) {
            errors.add(Error("A variable or input named '${it.name}' has been already declared at line ${varsByName[it.name]}",
                    it.position!!))
        } else {
            varsByName[it.name] = it.position!!.start.line
        }
    }
    this.specificProcess(InputDeclaration::class.java) {
        if (varsByName.containsKey(it.name)) {
            errors.add(Error("A variable or input named '${it.name}' has been already declared at line ${varsByName[it.name]}",
                    it.position!!))
        } else {
            varsByName[it.name] = it.position!!.start.line
        }
    }

    // check a variable is not referred before being declared
//    this.specificProcess(VarReference::class.java) {
//        if (!varsByName.containsKey(it.name)) {
//            errors.add(Error("There is no variable named '${it.name}'", it.position!!))
//        } else if (it.isBefore(varsByName[it.name]!!)) {
//            errors.add(Error("You cannot refer to variable '${it.name}' before its declaration", it.position!!))
//        }
//    }
//    this.specificProcess(Assignment::class.java) {
//        if (!varsByName.containsKey(it.name)) {
//            errors.add(Error("There is no variable named '${it.name}'", it.position!!))
//        } else if (it.isBefore(varsByName[it.name]!!)) {
//            errors.add(Error("You cannot refer to variable '${it.name}' before its declaration", it.position!!))
//        }
//    }

    return errors
}