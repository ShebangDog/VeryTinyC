package front

sealed class Either<out L, out R> {
    class Left<L>(val value: L) : Either<L, Nothing>()
    class Right<R>(val value: R) : Either<Nothing, R>()

    companion object {
        fun <L, F, S, T, R> flatMap(
            first: Either<L, F>,
            second: Either<L, S>,
            third: Either<L, T>,
            transform: (F, S, T) -> R
        ): Either<L, R> = run {
            first.flatMap { f ->
                second.flatMap { s ->
                    third.map { t ->
                        transform(f, s, t)
                    }
                }
            }
        }
    }
}

inline fun <L, R, T> Either<L, R>.map(transform: (R) -> T): Either<L, T> = when (this) {
    is Either.Left -> Either.Left(value)
    is Either.Right -> Either.Right(transform(value))
}

fun <L, R> Either<L, R>.getOrElse(default: R): R = when (this) {
    is Either.Left -> default
    is Either.Right -> value
}

inline fun <L, R, T> Either<L, R>.flatMap(transform: (R) -> Either<L, T>): Either<L, T> = when (this) {
    is Either.Left -> Either.Left(this.value)
    is Either.Right -> transform(this.value)
}
