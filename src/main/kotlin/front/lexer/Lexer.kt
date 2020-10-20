package front.lexer

import front.Either
import front.getOrElse
import front.map

object Lexer {

    fun tokenize(inputStringList: List<String>): List<Either<TokenizeError, Token>> {
        fun recurse(
            inputStringList: List<String>,
            result: List<Either<TokenizeError, Token>>
        ): List<Either<TokenizeError, Token>> = when (inputStringList.isEmpty()) {
            true -> result
            false -> {
                val head = inputStringList.first()

                val token = when {
                    Token.Newline.isNewline(head) -> Either.Right(Token.Newline)
                    Token.WhiteSpace.isWhiteSpace(head) -> Either.Right(Token.WhiteSpace)
                    Token.Operator.isOperator(head) -> Token.Operator.of(head)
                    Token.Number.isNumber(head) -> Token.Number.of(inputStringList)
                    Token.Reserved.isReserved(head) -> Token.Reserved.of(head)
                    Token.Id.isId(inputStringList) -> Token.Id.of(inputStringList)

                    else -> Either.Left(TokenizeError.NoMatchError(head))
                }

                val consumed = when (token) {
                    is Either.Left -> token.value.rawString.length
                    is Either.Right -> token.value.rawString.length
                }

                recurse(inputStringList.drop(consumed), result + (token))
            }
        }

        return recurse(inputStringList.toSplitBySingle(), emptyList())
            .filter { either -> either.map { it.isNotType<Token.WhiteSpace>() }.getOrElse(true) }
    }

    private fun List<String>.toSplitBySingle() = this.joinToString(" ").split("")
}