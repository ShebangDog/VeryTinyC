import front.Either
import front.lexer.Lexer
import front.lexer.Token
import front.lexer.TokenizeError
import front.parser.Node
import front.parser.ParseError
import front.parser.Parser
import java.io.BufferedReader
import java.io.File

typealias Tokenized = Either<TokenizeError, Token>

fun main() {
    val testFiles = listOf("expr.y")

    testFiles.map { it.toPath() }
        .forEach { testFilePath ->
            val inputFile: BufferedReader = File(testFilePath).bufferedReader()

            val readLines = inputFile.readLines()

            readLines
                .mapIndexed { index, line -> line + if (index == readLines.size - 1) "" else "\n" }
                .let { Lexer.tokenize(it) }
                .filterByToken()
                .dropWhile { it.isType<Token.NewLine>() }
                .dropLastWhile { it.isType<Token.NewLine>() }
                .let { Parser.parse(it) }
                .run { println(makeString()) }
        }
}

private fun List<Either<TokenizeError, Token>>.filterByToken(): List<Token> = this.mapNotNull {
    when (it) {
        is Either.Left -> null
        is Either.Right -> it.value
    }
}

private fun Tokenized.make(): String = when (this) {
    is Either.Left -> this.value.message()
    is Either.Right -> "'${this.value}'"
}

private fun Either<ParseError, Node>.makeString() = when (this) {
    is Either.Left -> value.message()
    is Either.Right -> value.makeString(Node.OrderType.PostOrder)
}

private fun String.toPath() = "./src/test/resources/$this"