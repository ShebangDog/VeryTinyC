package front

sealed class Either<L, out R> {
    class Left<L, R>(val message: L) : Either<L, R>()
    class Right<L, R>(val value: R) : Either<L, R>()
}