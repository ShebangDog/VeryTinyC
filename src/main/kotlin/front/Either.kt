package front

sealed class Either<out L, out R> {
    class Left<L>(val message: L) : Either<L, Nothing>()
    class Right<R>(val value: R) : Either<Nothing, R>()
}

inline fun <L, R, T> Either<L, R>.map(transform: (R) -> T): Either<L, T> = when (this) {
    is Either.Left -> Either.Left(message)
    is Either.Right -> Either.Right(transform(value))
}

fun <L, R> Either<L, R>.getOrElse(default: R): R = when (this) {
    is Either.Left -> default
    is Either.Right -> value
}