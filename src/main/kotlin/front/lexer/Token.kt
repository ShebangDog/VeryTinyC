package front.lexer

import front.Either

sealed class Token(val rawString: String) {

    class Number(val value: Int) : Token(rawString = value.toString()) {
        companion object {
            val numberList = (0..9).toList().map { it.toString() }

            fun isNumber(rawString: String): Boolean = numberList.contains(rawString)
        }
    }

    sealed class Operator(val value: String) : Token(rawString = value) {
        init {
            require(isOperator(value))
        }

        companion object {
            private const val plus = "+"
            private const val minus = "-"
            private const val multiple = "*"
            private const val divide = "/"

            val operatorList = listOf(plus, minus, multiple, divide)

            fun of(value: String): Either<TokenizeError, Operator> = when {
                Primary.primaryOperatorList.contains(value) -> Either.Right(Primary(value))
                Secondary.secondaryOperatorList.contains(value) -> Either.Right(Secondary(value))
                else -> Either.Left(TokenizeError.OperatorError(value))
            }

            fun isOperator(value: String): Boolean = operatorList.contains(value)
        }

        class Primary(value: String) : Operator(value) {
            init {
                require(primaryOperatorList.contains(value))
            }

            companion object {
                val primaryOperatorList = listOf(multiple, divide)
            }
        }

        class Secondary(value: String) : Operator(value) {
            init {
                require(secondaryOperatorList.contains(value))
            }

            companion object {
                val secondaryOperatorList = listOf(plus, minus)
            }
        }
    }

    inline fun <reified T : Token> isType(): Boolean = this is T

    override fun toString() = when (this) {
        is Operator -> value
        is Number -> value.toString()
    }
}