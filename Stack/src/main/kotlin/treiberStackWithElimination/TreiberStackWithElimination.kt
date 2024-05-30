package treiberStackWithElimination

import Node
import kotlinx.atomicfu.atomic
import java.util.concurrent.TimeoutException

const val ELIMINATION_ARRAY_SIZE = 4

class TreiberStackWithElimination<T> {
    private var head = atomic<Node<T>?>(null)

    private val eliminationArray = EliminationArray<T>(ELIMINATION_ARRAY_SIZE)

    fun push(value: T) {
        while (true) {
            val curHead = head.value
            val newHead = Node(curHead, value)
            if (head.compareAndSet(curHead, newHead)) {
                return
            }

            try {
                eliminationArray.visit(value)
            }
            catch (ex: TimeoutException) {
                continue
            }
        }
    }

    fun pop(): T? {
        while (true) {
            val curHead = head.value
            val newHead = curHead?.next
            if (head.compareAndSet(curHead, newHead)) {
                return curHead?.value
            }

            val visitResult = eliminationArray.visit(null)
            if (visitResult.isSuccess) {
                visitResult.getOrNull()?.let { return it }
            }
        }
    }

    fun top(): T? {
        return head.value?.value
    }
}
