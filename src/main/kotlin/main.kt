import front.Either
import front.emitter.Emitter
import front.lexer.Lexer
import front.lexer.Token
import front.lexer.TokenizeError
import front.parser.Node
import front.parser.ParseError
import front.parser.Parser
import java.io.BufferedReader
import java.io.File

private fun String.inResource(): String {
    val resourcePath = "./../../src/main/resources/"

    return resourcePath + this
}

private fun String.toPath() = "./$this"

fun main(args: Array<String>) {
    val fileName = "expr.y"
    val filePath = fileName.inResource()
            .let { args.first().toPath() }

    val inputFile: BufferedReader = File(filePath).bufferedReader()

    inputFile.readLines()
            .let { Lexer.tokenize(it) }
//        .also { tokenList -> tokenList.forEach(::printEitherToken) }
            .filterByToken()
//        .also { list -> list.forEach { println(it) }; println("") }
            .let { Parser.parse(it) }
            .makeString()
            .let { Emitter("out.c").emit(it.split(" ")) }

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