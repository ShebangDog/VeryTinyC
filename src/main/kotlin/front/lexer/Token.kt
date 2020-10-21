package front.lexer

import front.Either
import util.makeString
import util.toCharList

sealed class Token(val rawString: String) {

    class Number private constructor(val value: Int) : Token(rawString = value.toString()) {
        companion object {
            private val numberList = (0..9).toList().joinToString("") { it.toString() }.toCharList()

            fun of(charList: List<Char>) = charList
                .takeWhile { isNumber(it) }
                .makeString()
                .let {
                    when {
                        (it.length > 1 && it[0] == '0') -> Either.Left(TokenizeError.StartZeroError(it))
                        else -> Either.Right(Number(it.toInt()))
                    }
                }

            fun isNumber(char: Char): Boolean = numberList.contains(char)
        }
    }

    sealed class Operator(val value: String) : Token(rawString = value) {
        init {
            require(isOperator(value.toCharList()))
        }

        companion object {
            private const val plus = "+"
            private const val minus = "-"
            private const val multiple = "*"
            private const val divide = "/"
            private const val assign = "="
            private const val increment = "++"

            val operatorList = listOf(plus, minus, multiple, divide, assign, increment)

            fun of(charList: List<Char>): Either<TokenizeError, Operator> {
                fun recurse(string: String): String = when {
                    string.isEmpty() -> string
                    else -> {
                        if (operatorList.contains(string)) string
                        else recurse(string.dropLast(1))
                    }
                }

                val value = charList
                    .take(operatorList.maxOf { it.length })
                    .makeString()
                    .let { recurse(it) }

                return when {
                    Primary.primaryOperatorList.contains(value) -> Either.Right(Primary(value))
                    Secondary.secondaryOperatorList.contains(value) -> Either.Right(Secondary(value))
                    Tertiary.tertiaryOperatorList.contains(value) -> Either.Right(Tertiary(value))
                    else -> Either.Left(TokenizeError.OperatorError(value))
                }
            }

            fun isOperator(charList: List<Char>): Boolean {
                fun recurse(string: String): Boolean = when {
                    string.isEmpty() -> false
                    else -> {
                        if (operatorList.contains(string)) true
                        else recurse(string.dropLast(1))
                    }
                }

                val string = charList
                    .take(operatorList.maxOf { it.length })
                    .makeString()

                return recurse(string)
            }
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

        class Tertiary(value: String) : Operator(value) {
            init {
                require(tertiaryOperatorList.contains(value))
            }

            companion object {
                val tertiaryOperatorList = listOf(assign, increment)
            }
        }
    }

    sealed class Reserved(val value: String) : Token(rawString = value) {
        companion object {
            private fun reservedList() = Parentheses.parenthesesList + Type.typeNameList

            fun of(charList: List<Char>): Either<TokenizeError, Reserved> {
                fun recurse(string: String): String = when {
                    string.isEmpty() -> string
                    else -> {
                        if (reservedList().contains(string)) string
                        else recurse(string.dropLast(1))
                    }
                }

                val value = charList
                    .take(reservedList().maxOf { it.length })
                    .makeString()
                    .let { recurse(it) }

                return when {
                    isReserved(value) -> {
                        when {
                            Parentheses.isParentheses(value) -> Parentheses.of(value)

                            Type.isType(value) -> Type.of(value)

                            else -> Either.Left(TokenizeError.NoMatchError(value))
                        }
                    }

                    else -> Either.Left(TokenizeError.NoMatchError(value))
                }
            }

            fun isReserved(charList: List<Char>): Boolean {
                fun recurse(string: String): Boolean = when {
                    string.isEmpty() -> false
                    else -> {
                        if (reservedList().contains(string)) true
                        else recurse(string.dropLast(1))
                    }
                }

                val took = charList
                    .take(reservedList().maxOf { it.length })
                    .makeString()

                return recurse(took)
            }

            fun isReserved(reserved: String): Boolean = reservedList().contains(reserved)
        }

        sealed class Parentheses(value: String) : Reserved(value) {
            init {
                require(parenthesesList.contains(value))
            }

            companion object {
                const val open = "("
                const val close = ")"

                val parenthesesList = listOf(open, close)

                fun of(value: String) = when (value) {
                    open -> Either.Right(Open)
                    close -> Either.Right(Close)
                    else -> Either.Left(TokenizeError.NoMatchError(value))
                }

                fun isParentheses(parentheses: String) = parenthesesList.contains(parentheses)
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

                fun of(value: String): Either<TokenizeError, Reserved> = when {
                    !isType(value) -> Either.Left(TokenizeError.NoMatchError(value))
                    value == integer -> Either.Right(Integer)
                    else -> Either.Left(TokenizeError.NoMatchError(value))
                }

                fun isType(typeName: String) = typeNameList.contains(typeName)
            }

            object Integer : Type(integer)
        }
    }

    class Id(val value: String) : Token(rawString = value) {
        init {
            require(isId(value))
        }

        companion object {
            fun of(charList: List<Char>): Either<TokenizeError, Id> {
                val idName = charList
                    .takeWhile { !WhiteSpace.isWhiteSpace(it) && !Newline.isNewline(it) }
                    .makeString()

                if (!isId(idName)) {
                    val head = charList.first()
                    return when {
                        head.isLetter() -> Either.Left(TokenizeError.ReservedError(idName))
                        else -> Either.Left(TokenizeError.StartNumberError(idName))
                    }
                }

                return if (Reserved.isReserved(idName)) Either.Left(TokenizeError.ReservedError(idName))
                else Either.Right(Id(idName))
            }

            fun isId(charList: List<Char>): Boolean {
                val head = charList.firstOrNull()
                if (head?.isLetter() != true) return false

                val idName = charList
                    .takeWhile { !WhiteSpace.isWhiteSpace(it) && !Newline.isNewline(it) }

                return !Reserved.isReserved(idName)
            }

            fun isId(idName: String): Boolean = isId(idName.toCharList())
        }
    }

    object WhiteSpace : Token(rawString = " ") {
        fun isWhiteSpace(char: Char) = !Newline.isNewline(char) && char.toString().isBlank()
    }

    object Newline : Token(rawString = "\n") {
        fun isNewline(value: Char) = value == '\n'
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