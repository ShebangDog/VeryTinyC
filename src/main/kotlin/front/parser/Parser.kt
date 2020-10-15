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
        fun recurse(tokenList: List<Token>, left: Either<ParseError, Node>): Either<ParseError, Node> {
            val head = tokenList.firstOrNull() ?: return left
            val tail = tokenList.drop(1)

            return when (head) {
                is Token.Operator.Secondary -> {
                    val operator = ope(head)
                    val (right, consumedList) = term(tail)

                    val partialTree = Either.flatMap(operator, left, right) { v, l, r ->
                        Node.Internal(v.value, l, r)
                    }

                    recurse(consumedList, partialTree)
                }

                else -> Either.Left(ParseError.NoMatchError(head.rawString))
            }
        }

        val (left, consumedList) = term(tokenList)

        return recurse(consumedList, left)
    }

    private fun term(tokenList: List<Token>): Pair<Either<ParseError, Node>, List<Token>> {
        fun recurse(tokenList: List<Token>, left: Either<ParseError, Node>): Pair<Either<ParseError, Node>, List<Token>> {
            val head = tokenList.firstOrNull() ?: return left to tokenList
            val tail = tokenList.drop(1)

            return when (head) {
                is Token.Operator.Primary -> {
                    val operator = ope(head)
                    val right = factor(tail.first())

                    val partialTree = Either.flatMap(operator, left, right) { v, l, r ->
                        Node.Internal(v.value, l, r)
                    }

                    recurse(tail.drop(1), partialTree)
                }

                else -> left to tokenList
            }
        }

        val head = tokenList.first()
        val tail = tokenList.drop(1)

        return when (head) {
            is Token.Number -> {
                val left = factor(head)

                if (tail.isEmpty()) left to tail
                else if (!tail.first().isType<Token.Operator.Primary>()) left to tail
                else recurse(tail, left)
            }

            else -> Either.Left(ParseError.NoMatchError(head.rawString)) to tail
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