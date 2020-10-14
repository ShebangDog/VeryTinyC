package front.parser

import front.lexer.Token

object Parser {

    fun parse(tokenList: List<Token>): Node {
        val head = tokenList.first()

        return when (head) {
            is Token.Number -> expr(tokenList)
            is Token.Operator -> TODO("error while parsing")
        }
    }

    private fun expr(tokenList: List<Token>): Node {
        val head = tokenList.first()

        return when (head) {
            is Token.Operator -> TODO("error while parsing")
            is Token.Number -> {
                val left = num(head)

                if (tokenList.drop(1).isEmpty() || !tokenList[1].isType<Token.Operator>()) left
                else {
                    val value = ope(tokenList[1]).value
                    val right = expr(tokenList.drop(2))
                    Node.Internal(value, left, right)
                }
            }
        }
    }

    private fun num(token: Token) = when (token) {
        is Token.Number -> Node.Leaf(Node.NodeValue.Number(token.value))
        is Token.Operator -> TODO("error while parsing")
    }

    private fun ope(token: Token) = when (token) {
        is Token.Operator -> Node.Leaf(Node.NodeValue.Operator(token.value))
        is Token.Number -> TODO("error while parsing")
    }
}