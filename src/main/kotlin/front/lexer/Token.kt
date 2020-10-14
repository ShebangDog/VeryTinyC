package front.lexer

sealed class Token(val rawString: String) {

    class Number(val value: Int) : Token(rawString = value.toString()) {
        companion object {
            val numberList = (0..9).toList().map { it.toString() }

            fun isNumber(rawString: String): Boolean = numberList.contains(rawString)
        }
    }

    class Operator(val value: String) : Token(rawString = value) {
        companion object {
            val operatorList = listOf("+", "-", "*", "/")

            fun isOperator(value: String): Boolean = operatorList.contains(value)
        }
    }

    inline fun <reified T : Token> isType(): Boolean = this is T

    override fun toString() = when (this) {
        is Operator -> value
        is Number -> value.toString()
    }
}