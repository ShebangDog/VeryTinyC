package front.lexer

import front.Either

sealed class Token(val rawString: String) {

    class Number private constructor(val value: Int) : Token(rawString = value.toString()) {
        companion object {
            val numberList = (0..9).toList().map { it.toString() }

            fun of(stringList: List<String>) = stringList
                .takeWhile { Number.isNumber(it) }
                .joinToString("")
                .let {
                    when {
                        (it.length > 1 && it[0] == '0') -> Either.Left(TokenizeError.StartZeroError(it))
                        else -> Either.Right(Number(it.toInt()))
                    }
                }

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

    sealed class Reserved(val value: String) : Token(rawString = value) {
        companion object {
            fun reservedList() = Parentheses.parenthesesList

            fun of(value: String): Either<TokenizeError, Reserved> = when {
                Parentheses.parenthesesList.contains(value) -> {
                    val parentheses = if (Parentheses.open == value) Parentheses.Open else Parentheses.Close

                    Either.Right(parentheses)
                }

                else -> Either.Left(TokenizeError.NoMatchError(value))
            }

            fun isReserved(value: String): Boolean = Parentheses.parenthesesList.contains(value)
        }

        sealed class Parentheses(value: String) : Reserved(value) {
            init {
                require(parenthesesList.contains(value))
            }

            companion object {
                val open = "("
                val close = ")"

                val parenthesesList = listOf(open, close)
            }

            object Open : Parentheses(open)
            object Close : Parentheses(close)
        }
    }

    class Id(val value: String) : Token(rawString = value) {
        companion object {
            fun of(stringList: List<String>): Either<TokenizeError, Id> {
                val head = stringList.first()
                if (!isId(head)) return Either.Left(TokenizeError.StartNumberError(head))

                val idName = stringList
                    .takeWhile { !WhiteSpace.isWhiteSpace(it) && !Newline.isNewline(it) }
                    .joinToString("")

                return if (Reserved.isReserved(idName)) Either.Left(TokenizeError.ReservedError(idName))
                else Either.Right(Id(idName))
            }

            fun isId(rawString: String) = rawString.first().isLetter()
        }
    }

    object WhiteSpace : Token(rawString = " ") {
        fun isWhiteSpace(value: String) = !Newline.isNewline(value) && value.isBlank()
    }

    object Newline : Token(rawString = "\n") {
        fun isNewline(value: String) = value == "\n"
    }

    inline fun <reified T : Token> isType(): Boolean = this is T

    inline fun <reified T : Token> isNotType(): Boolean = !isType<T>()

    override fun toString() = when (this) {
        is WhiteSpace -> this.rawString
        is Newline -> this.rawString

        is Operator -> value
        is Reserved -> value

        is Id -> value
        is Number -> value.toString()
    }
}