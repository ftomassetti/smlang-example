package me.tomassetti.presentation

class Dummy { }

fun readExampleCode() = Dummy::class.java.classLoader.getResourceAsStream("mysm.sm").bufferedReader().use { it.readText() }