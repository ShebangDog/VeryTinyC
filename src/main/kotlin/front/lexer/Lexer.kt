package front.lexer

import front.Either

object Lexer {

    fun tokenize(inputStringList: List<String>): List<Either<TokenizeError, Token>> {
        fun recurse(
                inputStringList: List<String>,
                result: List<Either<TokenizeError, Token?>>
        ): List<Either<TokenizeError, Token?>> =
                when (inputStringList.isEmpty()) {
                    true -> result
                    false -> {
                        val head = inputStringList.first()

                        val token = when {
                            head.isBlank() -> Either.Right<Token?>(null)
                            Token.Operator.isOperator(head) -> Token.Operator.of(head)
                            Token.Number.isNumber(head) -> {
                                inputStringList
                                        .takeWhile { Token.Number.isNumber(it) }
                                        .joinToString("")
                                        .let {
                                            when {
                                                (it.length > 1 && it[0] == '0') -> Either.Left(TokenizeError.StartZeroError(it))
                                                else -> Either.Right(Token.Number(it.toInt()))
                                            }
                                        }
                            }

                            else -> Either.Left(TokenizeError.NoMatchError(head))
                        }

                        val consumed = when (token) {
                            is Either.Left -> token.value.rawString.length
                            is Either.Right -> token.value?.rawString?.length
                        }

                        recurse(
                                inputStringList.drop(consumed ?: 1),
                                result + (token)
                        )
                    }
                }

        return recurse(inputStringList.toSplitBySingle(), emptyList()).mapNotNull {
            when (it) {
                is Either.Left -> Either.Left(it.value)
                is Either.Right -> it.value?.let { internal -> Either.Right(internal) }
            }
        }
    }

    private fun List<String>.toSplitBySingle() = this.joinToString(" ").split("")

}
