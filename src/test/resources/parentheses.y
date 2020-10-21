    fun parse(tokenList: List<Token>): List<Either<ParseError, Node>> {
        fun recurse(
            tokenList: List<Token>,
            result: List<Either<ParseError, Node>> = emptyList()
        ): List<Either<ParseError, Node>> {
            return if (tokenList.isEmpty()) result
            else when (val head = tokenList.first()) {
                is Token.Number -> {
                    val (node, consumedList) = expr(tokenList)

                    recurse(consumedList, result + node)
                }

                is Token.Reserved.Parentheses.Open -> {
                    val (node, consumedList) = expr(tokenList)

                    recurse(consumedList, result + node)
                }

                else -> result + Either.Left(ParseError.NoMatchError(head.rawString))
            }
        }

        return recurse(tokenList)
    }
