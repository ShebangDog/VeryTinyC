import front.Lexer
import java.io.BufferedReader
import java.io.File

fun String.inResource(): String {
    val resourcePath = "./src/main/resources/"

    return resourcePath + this
}


fun main(args: Array<String>) {
    val fileName = "main.c"
    val inputFile: BufferedReader = File(fileName.inResource()).bufferedReader()

    inputFile.readLines()
        .let { Lexer.tokenize(it) }
        .forEach { println(it) }
}