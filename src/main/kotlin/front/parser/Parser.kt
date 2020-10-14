package front.parser

import front.Either
import front.lexer.Token

object Parser {

    fun parse(tokenList: List<Token>): Either<ParseError, Node> {
        val head = tokenList.first()

        return when (head) {
            is Token.Number -> expr(tokenList)
            else -> Either.Left(ParseError.NoMatchError(head.rawString))
        }
    }

    private fun expr(tokenList: List<Token>): Either<ParseError, Node> {
        val head = tokenList.first()

        return when (head) {
            is Token.Number -> {
                val left = num(head)

                if (tokenList.drop(1).isEmpty() || !tokenList[1].isType<Token.Operator>()) left
                else {
                    val value = ope(tokenList[1])
                    val right = expr(tokenList.drop(2))

                    Either.flatMap(value, left, right) { v, l, r ->
                        Node.Internal(v.value, l, r)
                    }
                }
            }

            else -> Either.Left(ParseError.NoMatchError(head.rawString))
        }
    }

    private fun num(token: Token) = when (token) {
        is Token.Number -> Either.Right(Node.Leaf(Node.NodeValue.Number(token.value)))
        is Token.Operator -> Either.Left(ParseError.NoMatchError(token.rawString))
    }

    private fun ope(token: Token) = when (token) {
        is Token.Operator -> Either.Right(Node.Leaf(Node.NodeValue.Operator(token.value)))
        is Token.Number -> Either.Left(ParseError.NoMatchError(token.rawString))
    }
}