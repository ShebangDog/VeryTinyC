package front.lexer

sealed class TokenizeError(private val tag: String, val rawString: String) {
    class NoMatchError(rawString: String) : TokenizeError("NoMatchError", rawString)

    class StartZeroError(rawString: String) : TokenizeError("StartZeroError", rawString)

    class OperatorError(rawString: String) : TokenizeError("OperatorError", rawString)

    fun message() = "$tag: $rawString"
}