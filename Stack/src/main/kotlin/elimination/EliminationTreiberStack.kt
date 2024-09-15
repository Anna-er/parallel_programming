package elimination

import common.Stack
import common.Node

import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicReference


class EliminationTreiberStack<T>(private val capacity: Int) : Stack<T> {
    private val eliminationArray = EliminationArray<T?>(capacity)
    private val head: AtomicReference<Node<T>?> = AtomicReference(null)

    override fun push(value: T) {
        while (true) {
            val curHead = head.get()
            val newHead = Node(value, curHead)
            if (head.compareAndSet(curHead, newHead)) {
                return
            }

            try {
                eliminationArray.visit(value, capacity) ?: return
            }
            catch (ex: TimeoutException) {
                continue
            }
        }
    }

    override fun pop(): T? {
        while (true) {
            val curHead = head.get()
            if (head.compareAndSet(curHead, curHead?.next)) {
                return curHead?.value
            }

            try {
                val otherValue = eliminationArray.visit(null, capacity)
                if (otherValue != null) {
                    return otherValue
                }
            }
            catch (ex: TimeoutException) {
                continue
            }
        }
    }

    override fun top(): T? = head.get()?.value
}
