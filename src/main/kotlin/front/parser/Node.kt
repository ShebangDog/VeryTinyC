package front.parser

sealed class Node(val value: NodeValue) {
    class Leaf(value: NodeValue) : Node(value)

    class Internal(value: NodeValue, val left: Node?, val right: Node?) : Node(value)

    sealed class NodeValue {
        class Number(val value: Int) : NodeValue()

        class Operator(val value: String) : NodeValue()

        override fun toString(): String = when (this) {
            is Number -> value.toString()
            is Operator -> value
        }
    }

    sealed class OrderType {
        object PreOrder : OrderType()
        object InOrder : OrderType()
        object PostOrder : OrderType()
    }

    fun makeString(orderType: OrderType, separator: String = " "): String = when (orderType) {
        is OrderType.PreOrder -> makeStringPreOrder(separator)
        is OrderType.InOrder -> makeStringInOrder(separator)
        is OrderType.PostOrder -> makeStringPostOrder(separator)
    }

    private fun makeStringPreOrder(separator: String): String = when (this) {
        is Leaf -> value.toString()
        is Internal -> listOf(
            value.toString(),
            left?.makeStringPreOrder(separator),
            right?.makeStringPreOrder(separator)
        ).joinToString(separator)
    }

    private fun makeStringInOrder(separator: String): String = when (this) {
        is Leaf -> value.toString()
        is Internal -> listOf(
            left?.makeStringInOrder(separator),
            value.toString(),
            right?.makeStringInOrder(separator)
        ).joinToString(separator)

    }

    private fun makeStringPostOrder(separator: String): String = when (this) {
        is Leaf -> value.toString()
        is Internal -> listOf(
            left?.makeStringPostOrder(separator),
            right?.makeStringPostOrder(separator),
            value.toString()
        ).joinToString(separator)

    }
}