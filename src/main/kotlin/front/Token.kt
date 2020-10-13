package front

sealed class Token {
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

    companion object {
        fun of(string: String): Token? {
            if (string.isBlank()) return null
            if (Number.isNumber(string)) return Number(string.toInt())
            if (Operator.isOperator(string)) return Operator(string)

            throw Exception()
        }
    }

    override fun toString() = when (this) {
        is Operator -> value
        is Number -> value.toString()
    }
}