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
        if (varsByName.containsKey(it.varName)) {
            errors.add(Error("A variable or input named '${it.varName}' has been already declared at line ${varsByName[it.varName]}",
                    it.position!!))
        } else {
            varsByName[it.varName] = it.position!!.start.line
        }
    }
    this.specificProcess(InputDeclaration::class.java) {
        if (varsByName.containsKey(it.inputName)) {
            errors.add(Error("A variable or input named '${it.inputName}' has been already declared at line ${varsByName[it.inputName]}",
                    it.position!!))
        } else {
            varsByName[it.inputName] = it.position!!.start.line
        }
    }

    // check a variable is not referred before being declared
//    this.specificProcess(VarReference::class.java) {
//        if (!varsByName.containsKey(it.varName)) {
//            errors.add(Error("There is no variable named '${it.varName}'", it.position!!))
//        } else if (it.isBefore(varsByName[it.varName]!!)) {
//            errors.add(Error("You cannot refer to variable '${it.varName}' before its declaration", it.position!!))
//        }
//    }
//    this.specificProcess(Assignment::class.java) {
//        if (!varsByName.containsKey(it.varName)) {
//            errors.add(Error("There is no variable named '${it.varName}'", it.position!!))
//        } else if (it.isBefore(varsByName[it.varName]!!)) {
//            errors.add(Error("You cannot refer to variable '${it.varName}' before its declaration", it.position!!))
//        }
//    }

    return errors
}