package front.parser

import front.Either
import front.lexer.Token

object Parser {

    fun parse(tokenList: List<Token>) = when (val head = tokenList.first()) {
        is Token.Number -> expr(tokenList).first
        is Token.Reserved.Parentheses.Open -> expr(tokenList).first
        else -> Either.Left(ParseError.NoMatchError(head.rawString))
    }

    private fun expr(tokenList: List<Token>): Pair<Either<ParseError, Node>, List<Token>> {
        fun recurse(
            tokenList: List<Token>,
            left: Either<ParseError, Node>
        ): Pair<Either<ParseError, Node>, List<Token>> {
            val head = tokenList.firstOrNull() ?: return left to tokenList
            val tail = tokenList.drop(1)

            return when (head) {
                is Token.Operator.Secondary -> {
                    val operator = ope(head)
                    val (right, consumedList) = term(newLine(tail))

                    val partialTree = Either.flatMap(operator, left, right) { v, l, r ->
                        Node.Internal(v.value, l, r)
                    }

                    recurse(consumedList, partialTree)
                }

                is Token.Reserved.Parentheses.Close -> left to tokenList

                is Token.Newline -> recurse(newLine(tokenList), left)

                else -> Either.Left(ParseError.NoMatchError(head.rawString)) to tokenList
            }
        }

        return when (val head = tokenList.first()) {
            is Token.Number -> {
                val (left, consumedList) = term(tokenList)
                recurse(consumedList, left)
            }

            is Token.Reserved.Parentheses.Open -> {
                val (left, consumedList) = term(newLine(tokenList))
                recurse(consumedList, left)
            }

            else -> Either.Left(ParseError.NoMatchError(head.rawString)) to tokenList
        }
    }

    private fun term(tokenList: List<Token>): Pair<Either<ParseError, Node>, List<Token>> {
        fun recurse(
            tokenList: List<Token>,
            left: Either<ParseError, Node>
        ): Pair<Either<ParseError, Node>, List<Token>> {
            val head = tokenList.firstOrNull() ?: return left to tokenList
            val tail = tokenList.drop(1)

            return when (head) {
                is Token.Operator.Primary -> {
                    val operator = ope(head)
                    val (right, consumed) = factor(newLine(tail))

                    val partialTree = Either.flatMap(operator, left, right) { v, l, r ->
                        Node.Internal(v.value, l, r)
                    }

                    recurse(consumed, partialTree)
                }

                else -> left to tokenList
            }
        }

        return when (val head = tokenList.first()) {
            is Token.Number -> {
                val (left, consumed) = factor(tokenList)

                if (consumed.isEmpty()) left to consumed
                else if (!consumed.first().isType<Token.Operator.Primary>()) left to consumed
                else recurse(consumed, left)
            }

            is Token.Reserved.Parentheses.Open -> {
                val (left, consumed) = factor(newLine(tokenList))
                recurse(consumed, left)
            }

            else -> Either.Left(ParseError.NoMatchError(head.rawString)) to tokenList
        }
    }

    private fun factor(tokenList: List<Token>): Pair<Either<ParseError, Node>, List<Token>> {
        val head = tokenList.first()
        val tail = tokenList.drop(1)

        return when (head) {
            is Token.Number -> number(head) to tail
            is Token.Reserved.Parentheses.Open -> {
                val (internalExpr, consumedTail) = expr(newLine(tail))
                val consumedExprHead = consumedTail.first()

                if (consumedExprHead.isType<Token.Reserved.Parentheses.Close>())
                    internalExpr to newLine(consumedTail.drop(1))
                else Either.Left(ParseError.NoMatchError(consumedExprHead.rawString)) to consumedTail
            }

            else -> Either.Left(ParseError.NoMatchError(head.rawString)) to tokenList
        }
    }

    private fun number(token: Token) = when (token) {
        is Token.Number -> Either.Right(Node.Leaf(Node.NodeValue.Number(token.value)))
        else -> Either.Left(ParseError.NoMatchError(token.rawString))
    }

    private fun ope(token: Token) = when (token) {
        is Token.Operator -> Either.Right(Node.Leaf(Node.NodeValue.Operator(token.value)))
        else -> Either.Left(ParseError.NoMatchError(token.rawString))
    }

    private fun newLine(tokenList: List<Token>) = tokenList.dropWhile { it.isType<Token.Newline>() }
}