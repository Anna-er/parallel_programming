package coarsegrained

import sequential.Tree
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Tree {
    private val lock = ReentrantLock()
    private val seqTree = Tree()

    fun insert(x: Int) {
        lock.withLock {
            seqTree.insert(x)
        }
    }

    fun find(x: Int): Boolean {
        lock.withLock {
            return seqTree.find(x)
        }
    }

    fun remove(x: Int) {
        lock.withLock {
            seqTree.remove(x)
        }
    }

    fun inOrderPrint() {
        lock.withLock {
            seqTree.inOrderPrint()
        }
    }

    fun isValid(): Boolean {
        lock.withLock {
            return seqTree.isValid()
        }
    }

    fun isEmpty(): Boolean {
        lock.withLock {
            return seqTree.isEmpty()
        }
    }

    

    companion object {
        fun newTree(): Tree {
            return Tree()
        }
    }
}
