package treiberStack

import Node
import kotlinx.atomicfu.atomic

class TreiberStack<T> {
    private val head = atomic<Node<T>?>(null)

    fun top(): T? {
        return head.value?.value
    }

    fun push(value: T) {
        while (true) {
            val curHead = head.value
            val newHead = Node(curHead, value)
            if (head.compareAndSet(curHead, newHead)){
                return
            }
        }
    }


    fun pop(): T? {
        while (true) {
            val curHead = head.value
            if (head.compareAndSet(curHead, curHead?.next)){
                return curHead?.value
            }
        }
    }
}
