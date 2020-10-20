package front.lexer

sealed class TokenizeError(private val tag: String, val rawString: String) {
    class NoMatchError(rawString: String) : TokenizeError("NoMatchError", rawString)

    class StartZeroError(rawString: String) : TokenizeError("StartZeroError", rawString)

    class OperatorError(rawString: String) : TokenizeError("OperatorError", rawString)

    class StartNumberError(rawString: String) : TokenizeError("StartNumberError", rawString)

    class ReservedError(rawString: String) : TokenizeError("ReservedError", rawString)

    fun message() = "$tag: $rawString"
}