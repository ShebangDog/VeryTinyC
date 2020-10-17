package front.parser

sealed class ParseError(private val tag: String, val rawString: String) {
    class NoMatchError(rawString: String) : ParseError("NoMatchError", rawString)

    fun message() = "$tag: $rawString while parsing"
}