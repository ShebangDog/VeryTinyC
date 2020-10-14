package front.lexer

import front.Either

object Lexer {

    fun tokenize(inputStringList: List<String>): List<Either<String, Token>> {
        fun recurse(inputStringList: List<String>, result: List<Either<String, Token?>>): List<Either<String, Token?>> =
            when (inputStringList.isEmpty()) {
                true -> result
                false -> recurse(
                    inputStringList.drop(1),
                    result + (Token.of(inputStringList.first()))
                )
            }

        return recurse(inputStringList.toSplitBySingle(), emptyList()).mapNotNull {
            when (it) {
                is Either.Left -> Either.Left(it.message)
                is Either.Right -> it.value?.let { internal -> Either.Right(internal) }
            }
        }
    }

    private fun List<String>.toSplitBySingle() = this.joinToString(" ").split("")
}
