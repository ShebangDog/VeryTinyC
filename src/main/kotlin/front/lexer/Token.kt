package front.lexer

import front.Either

sealed class Token {

    companion object {
        fun of(string: String): Either<String, Token?> {
            if (string.isBlank()) return Either.Right(null)
            if (Number.isNumber(string)) return Either.Right(Number(string.toInt()))
            if (Operator.isOperator(string)) return Either.Right(Operator(string))

            return Either.Left("got $string while tokenize".formatError())
        }

        private fun String.formatError(): String {
            val tag = "error"

            return "$tag: $this"
        }
    }

    class Number(val value: Int) : Token() {
        companion object {
            val numberList = (0..9).toList().map { it.toString() }

            fun isNumber(rawString: String): Boolean = numberList.contains(rawString)
        }
    }

    class Operator(val value: String) : Token() {
        companion object {
            val operatorList = listOf("+", "-", "*", "/")

            fun isOperator(value: String): Boolean = operatorList.contains(value)
        }
    }

    override fun toString() = when (this) {
        is Operator -> value
        is Number -> value.toString()
    }
}