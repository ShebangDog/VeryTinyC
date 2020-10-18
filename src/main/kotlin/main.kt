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

private fun String.toPath() = "./$this"

fun main(args: Array<String>) {
    val filePath = args.first().toPath()
    val inputFile: BufferedReader = File(filePath).bufferedReader()

    inputFile.readLines()
        .let { Lexer.tokenize(it) }
        .filterByToken()
        .let { Parser.parse(it) }
        .makeString()
        .let { Emitter().emit(it.split(" ")) }
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