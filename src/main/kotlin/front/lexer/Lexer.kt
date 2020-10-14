package front.lexer

import front.Either
import front.getOrElse
import front.map

object Lexer {

    fun tokenize(inputStringList: List<String>): List<Either<String, Token>> {
        fun recurse(inputStringList: List<String>, result: List<Either<String, Token?>>): List<Either<String, Token?>> =
            when (inputStringList.isEmpty()) {
                true -> result
                false -> {
                    val head = inputStringList.first()

                    val token = when {
                        head.isBlank() -> Either.Right<Token?>(null)
                        Token.Operator.isOperator(head) -> Either.Right<Token>(Token.Operator(head))
                        Token.Number.isNumber(head) -> {
                            inputStringList
                                .takeWhile { Token.Number.isNumber(it) }
                                .joinToString("")
                                .let {
                                    when {
                                        it.length > 1 && it.first() == '0' -> errorWhileTokenize(it)
                                        else -> Either.Right(Token.Number(it.toInt()))
                                    }
                                }
                        }

                        else -> errorWhileTokenize(head)
                    }

                    val consumed = token.map { it?.rawString?.length }.getOrElse(null)

                    recurse(
                        inputStringList.drop(consumed ?: 1),
                        result + (token)
                    )
                }
            }

        return recurse(inputStringList.toSplitBySingle(), emptyList()).mapNotNull {
            when (it) {
                is Either.Left -> Either.Left(it.message)
                is Either.Right -> it.value?.let { internal -> Either.Right(internal) }
            }
        }
    }

    private fun List<String>.toSplitBySingle() = this.joinToString(" ").split("")

    private fun errorWhileTokenize(gotString: String) =
        Either.Left("got $gotString while tokenize".formatError())

    private fun String.formatError(): String {
        val tag = "error"

        return "$tag: $this"
    }
}
