package front

object Lexer {

    fun tokenize(inputStringList: List<String>): List<Token> {
        fun recurse(inputStringList: List<String>, result: List<Token?>): List<Token?> =
            when (inputStringList.isEmpty()) {
                true -> result
                false -> recurse(
                    inputStringList.drop(1),
                    result + (Token.of(inputStringList.first()))
                )
            }

        return recurse(inputStringList.toSplitBySingle(), emptyList()).filterNotNull()
    }

    private fun List<String>.toSplitBySingle() = this.joinToString().split("")
}
