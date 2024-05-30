package stack

import Node
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop


class TreiberStack<T> {
    private val headNode = atomic<Node<T>?>(null)

    fun push(value: T): Unit = headNode.loop { curHead ->
        val newHead = Node(value, curHead)
        if (headNode.compareAndSet(curHead, newHead)) {
            return
        }
    }

    fun pop(): T? = headNode.loop { curHead ->
        val nextNode = curHead?.next
        if (headNode.compareAndSet(curHead, nextNode)) {
            return curHead?.value
        }
    }
}
