package util

fun List<String>.toCharList() = this.flatMap { it.toCharList() }

fun List<Char>.makeString() = this.joinToString("")

fun String.toCharList() = this.toCharArray().toList()