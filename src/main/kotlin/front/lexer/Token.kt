package front.lexer

sealed class Token {

    companion object {
        fun of(string: String): Token? {
            if (string.isBlank()) return null
            if (Number.isNumber(string)) return Number(string.toInt())
            if (Operator.isOperator(string)) return Operator(string)

            throw Exception()
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