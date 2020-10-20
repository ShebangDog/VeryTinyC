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
            private fun reservedList() = Parentheses.parenthesesList + Type.typeNameList

            fun of(stringList: List<String>): Either<TokenizeError, Reserved> {
                val value = stringList
                    .takeWhile { !WhiteSpace.isWhiteSpace(it) && !Newline.isNewline(it) }
                    .joinToString("")

                return when {
                    isReserved(value) -> {
                        when {
                            Parentheses.parenthesesList.contains(value) -> Parentheses.of(value)

                            Type.typeNameList.contains(value) -> Type.of(value)

                            else -> Either.Left(TokenizeError.NoMatchError(value))
                        }
                    }

                    else -> Either.Left(TokenizeError.NoMatchError(value))
                }
            }

            fun isReserved(stringList: List<String>): Boolean = reservedList().contains(
                stringList
                    .takeWhile { !WhiteSpace.isWhiteSpace(it) && !Newline.isNewline(it) }
                    .joinToString("")
            )

            fun isReserved(string: String): Boolean = isReserved(string.split("").filter { it != "" })
        }

        sealed class Parentheses(value: String) : Reserved(value) {
            init {
                require(parenthesesList.contains(value))
            }

            companion object {
                val open = "("
                val close = ")"

                val parenthesesList = listOf(open, close)

                fun of(value: String) = when (value) {
                    open -> Either.Right(Open)
                    close -> Either.Right(Close)
                    else -> Either.Left(TokenizeError.NoMatchError(value))
                }
            }

            object Open : Parentheses(open)
            object Close : Parentheses(close)
        }

        sealed class Type(value: String) : Reserved(value) {
            init {
                require(Companion.isType(value))
            }

            companion object {
                const val integer = "int"

                val typeNameList = listOf(integer)

                fun of(stringList: List<String>): Either<TokenizeError, Reserved> {
                    val typeName = stringList
                        .takeWhile { !WhiteSpace.isWhiteSpace(it) && !Newline.isNewline(it) }
                        .joinToString("")

                    if (!isType(typeName)) return Either.Left(TokenizeError.NoMatchError(typeName))

                    return if (typeName == integer) Either.Right(Integer)
                    else Either.Left(TokenizeError.NoMatchError(typeName))
                }

                fun of(typeName: String) = of(typeName.split("").filter { it != "" })

                fun isType(stringList: List<String>) = stringList
                    .takeWhile { !WhiteSpace.isWhiteSpace(it) && !Newline.isNewline(it) }
                    .joinToString("")
                    .let { typeNameList.contains(it) }

                private fun isType(typeName: String) = typeNameList.contains(typeName)
            }

            object Integer : Type(integer)
        }
    }

    class Id(val value: String) : Token(rawString = value) {
        init {
            require(isId(value))
        }

        companion object {
            fun of(stringList: List<String>): Either<TokenizeError, Id> {
                val idName = stringList
                    .takeWhile { !WhiteSpace.isWhiteSpace(it) && !Newline.isNewline(it) }
                    .joinToString("")

                if (!isId(idName)) {
                    val head = stringList.first()
                    return when {
                        head.first().isLetter() -> Either.Left(TokenizeError.ReservedError(head))
                        else -> Either.Left(TokenizeError.StartNumberError(head))
                    }
                }

                return if (Reserved.isReserved(idName)) Either.Left(TokenizeError.ReservedError(idName))
                else Either.Right(Id(idName))
            }

            fun isId(stringList: List<String>): Boolean {
                val head = stringList.first()
                if (head.firstOrNull()?.isLetter() != true) return false

                val idName = stringList
                    .takeWhile { !WhiteSpace.isWhiteSpace(it) && !Newline.isNewline(it) }
                    .joinToString("")

                return !Reserved.isReserved(idName)
            }

            private fun isId(idName: String): Boolean = isId(idName.split("")
                .filter { it != "" }
            )
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