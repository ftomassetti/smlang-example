package me.tomassetti.smlang.ast

import me.tomassetti.antlr.model.*
import java.util.*

fun checkForDuplicate(elementsByName: MutableMap<String, Int>, errors : MutableList<Error>, named: Named) {
    if (elementsByName.containsKey(named.name)) {
        errors.add(Error("A symbol or input named '${named.name}' has been already declared at line ${elementsByName[named.name]}",
                (named as Node).position!!))
    } else {
        elementsByName[named.name] = (named as Node).position!!.start.line
    }
}

fun StateMachine.validate() : List<Error> {
    val errors = LinkedList<Error>()

    // check a symbol or input is not duplicated
    val varsByName = HashMap<String, Int>()
    this.specificProcess(VarDeclaration::class.java) {
        checkForDuplicate(varsByName, errors, it)
    }
    this.specificProcess(InputDeclaration::class.java) {
        checkForDuplicate(varsByName, errors, it)
    }

    val eventsByName = HashMap<String, Int>()
    this.specificProcess(EventDeclaration::class.java) {
        checkForDuplicate(eventsByName, errors, it)
    }

    val statesByName = HashMap<String, Int>()
    this.specificProcess(EventDeclaration::class.java) {
        checkForDuplicate(statesByName, errors, it)
    }

    // check references
    this.specificProcess(ValueReference::class.java) {
        if (!it.symbol.tryToResolve(this.variables) && !it.symbol.tryToResolve(this.inputs)) {
            errors.add(Error("A reference to symbol or input '${it.symbol.name}' cannot be resolved", it.position!!))
        }
    }
    this.specificProcess(Assignment::class.java) {
        if (!it.variable.tryToResolve(this.variables)) {
            errors.add(Error("An assignment to symbol '${it.variable.name}' cannot be resolved", it.position!!))
        }
    }
    this.specificProcess(OnEventBlock::class.java) {
        if (!it.event.tryToResolve(this.events)) {
            errors.add(Error("A reference to event '${it.event.name}' cannot be resolved", it.position!!))
        }
    }
    this.specificProcess(OnEventBlock::class.java) {
        if (!it.destination.tryToResolve(this.states)) {
            errors.add(Error("A reference to state '${it.destination.name}' cannot be resolved", it.position!!))
        }
    }

    // if the type of a symbol is declared it has to be the same as the type of the initial value

    // we have exactly one start state
    if (this.states.filter { it.start }.size != 1) {
        errors.add(Error("A StateMachine should have exactly one start state", this.position!!))
    }

    return errors
}