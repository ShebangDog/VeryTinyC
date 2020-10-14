package front

sealed class Either<out L, out R> {
    class Left<L>(val message: L) : Either<L, Nothing>()
    class Right<R>(val value: R) : Either<Nothing, R>()
}