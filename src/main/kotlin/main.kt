import front.Either
import front.lexer.Lexer
import front.lexer.Token
import front.lexer.TokenizeError
import front.parser.Node
import front.parser.ParseError
import front.parser.Parser
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
//        .also { tokenList -> tokenList.forEach(::printEitherToken) }
        .filterByToken()
//        .also { list -> list.forEach { println(it) }; println("") }
        .let { Parser.parse(it) }
        .also { println(it.makeString()) }

}

private fun printEitherToken(either: Either<TokenizeError, Token>) {
    println(
        when (either) {
            is Either.Left -> either.value.message()
            is Either.Right -> either.value
        }
    )
}

private fun List<Either<TokenizeError, Token>>.filterByToken(): List<Token> = this.mapNotNull {
    when (it) {
        is Either.Left -> null
        is Either.Right -> it.value
    }
}

private fun Either<ParseError, Node>.makeString() = when (this) {
    is Either.Left -> value.message()
    is Either.Right -> value.makeString(Node.OrderType.PostOrder)
}