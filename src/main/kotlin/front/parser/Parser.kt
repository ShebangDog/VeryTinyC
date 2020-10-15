package front.parser

import front.Either
import front.lexer.Token

object Parser {

    fun parse(tokenList: List<Token>): Either<ParseError, Node> {
        val head = tokenList.first()

        return when (head) {
            is Token.Number -> term(tokenList)
            else -> Either.Left(ParseError.NoMatchError(head.rawString))
        }
    }

    private fun term(tokenList: List<Token>): Either<ParseError, Node> {
        fun recurse(tokenList: List<Token>, left: Either<ParseError, Node>): Either<ParseError, Node> {
            val head = tokenList.firstOrNull() ?: return left

            return when (head) {
                is Token.Operator -> {
                    val tail = tokenList.drop(1)

                    val operator = ope(head)
                    val right = factor(tail.first())

                    val partialTree = Either.flatMap(operator, left, right) { v, l, r ->
                        Node.Internal(v.value, l, r)
                    }

                    recurse(tail.drop(1), partialTree)
                }

                else -> Either.Left(ParseError.NoMatchError(head.rawString))
            }
        }

        val head = tokenList.first()

        return when (head) {
            is Token.Number -> {
                val left = factor(head)
                val tail = tokenList.drop(1)

                if (tail.isEmpty() || !tail.first().isType<Token.Operator>()) left
                else recurse(tail, left)
            }

            else -> Either.Left(ParseError.NoMatchError(head.rawString))
        }
    }

    private fun factor(token: Token) = when (token) {
        is Token.Number -> Either.Right(Node.Leaf(Node.NodeValue.Number(token.value)))
        else -> Either.Left(ParseError.NoMatchError(token.rawString))
    }

    private fun ope(token: Token) = when (token) {
        is Token.Operator -> Either.Right(Node.Leaf(Node.NodeValue.Operator(token.value)))
        else -> Either.Left(ParseError.NoMatchError(token.rawString))
    }
}