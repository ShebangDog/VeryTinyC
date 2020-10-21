package front.lexer

import front.Either
import front.getOrElse
import front.map
import util.toCharList

object Lexer {

    fun tokenize(inputStringList: List<String>): List<Either<TokenizeError, Token>> {
        fun recurse(
            inputCharList: List<Char>,
            result: List<Either<TokenizeError, Token>>
        ): List<Either<TokenizeError, Token>> = when (inputCharList.isEmpty()) {
            true -> result
            false -> {
                val head = inputCharList.first()

                val token = when {
                    Token.Newline.isNewline(head) -> Either.Right(Token.Newline)
                    Token.WhiteSpace.isWhiteSpace(head) -> Either.Right(Token.WhiteSpace)
                    Token.Operator.isOperator(inputCharList) -> Token.Operator.of(inputCharList)
                    Token.Number.isNumber(head) -> Token.Number.of(inputCharList)
                    Token.Reserved.isReserved(inputCharList) -> Token.Reserved.of(inputCharList)
                    Token.Id.isId(inputCharList) -> Token.Id.of(inputCharList)

                    else -> Either.Left(TokenizeError.NoMatchError(head.toString()))
                }

                val consumed = when (token) {
                    is Either.Left -> token.value.rawString.length
                    is Either.Right -> token.value.rawString.length
                }

                recurse(inputCharList.drop(consumed), result + (token))
            }
        }

        return recurse(inputStringList.toCharList(), emptyList())
            .filter { either -> either.map { it.isNotType<Token.WhiteSpace>() }.getOrElse(true) }
    }
}