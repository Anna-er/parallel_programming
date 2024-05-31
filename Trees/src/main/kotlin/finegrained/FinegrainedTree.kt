package finegrained

import java.util.concurrent.locks.ReentrantLock

class Node(var value: Int) {
    var left: Node? = null
    var right: Node? = null
    val lock = ReentrantLock()

    fun lock() {
        lock.lock()
    }

    fun unlock() {
        lock.unlock()
    }
}

class Tree {
    private var root: Node? = null
    private val lock = ReentrantLock()

    private fun lockTree() {
        lock.lock()
    }

    private fun unlockTree() {
        lock.unlock()
    }

    private fun findHelper(x: Int): Pair<Node?, Node?> {
        lockTree()
        if (root == null) {
            return Pair(null, null)
        }
        root!!.lock()
        var curr: Node? = root
        var prev: Node? = null
        while (curr != null && curr.value != x) {
            val temp = prev
            prev = curr
            if (x < curr.value) {
                curr.left?.lock()
                curr = curr.left
            } else {
                curr.right?.lock()
                curr = curr.right
            }
            if (temp == null) {
                unlockTree()
            } else {
                temp.unlock()
            }
        }
        return Pair(curr, prev)
    }

    fun insert(x: Int) {
        val (curr, prev) = findHelper(x)
        val node = Node(x)
        if (root == null) {
            root = node
            unlockTree()
            return
        }
        if (prev == null) {
            unlockTree()
        } else {
            prev.unlock()
        }
        if (curr != null) {
            curr.unlock()
            return
        }
        if (x < prev!!.value) {
            prev.left = node
        } else {
            prev.right = node
        }
    }

    fun find(x: Int): Boolean {
        val (curr, prev) = findHelper(x)
        if (prev == null) {
            unlockTree()
        } else {
            prev.unlock()
        }
        return curr != null.also { curr?.unlock() }
    }

    fun remove(x: Int) {
        val (curr, prev) = findHelper(x)
        if (prev == null) {
            unlockTree()
        } else {
            prev.unlock()
        }
        curr?.let {
            if (it.left == null && it.right == null) {
                if (it == root) {
                    root = null
                } else if (it.value < prev!!.value) {
                    prev.left = null
                } else {
                    prev.right = null
                }
                return
            }

            if (it.left == null) {
                if (it == root) {
                    root = it.right
                } else if (it.value < prev!!.value) {
                    prev.left = it.right
                } else {
                    prev.right = it.right
                }
                return
            }

            if (it.right == null) {
                if (it == root) {
                    root = it.left
                } else if (it.value < prev!!.value) {
                    prev.left = it.left
                } else {
                    prev.right = it.left
                }
                return
            }

            it.unlock()
            it.right!!.lock()
            var succParent = it
            var succ = it.right
            while (succ!!.left != null) {
                val temp = succParent
                succParent = succ
                succ.left!!.lock()
                succ = succ.left
                if (temp != it) {
                    temp!!.unlock()
                }
            }

            if (succParent != it) {
                succParent!!.left = succ.right
                succParent.unlock()
            } else {
                succParent.right = succ.right
            }
            it.value = succ.value
            succ.unlock()
        }
    }

    private fun inOrderPrint(node: Node?) {
        if (node == null) return
        node.lock()
        inOrderPrint(node.left)
        print("${node.value} ")
        inOrderPrint(node.right)
        node.unlock()
    }


    fun inOrderPrint() {
        inOrderPrint(root)
        println()
    }

    private fun isValid(node: Node?): Boolean {
        node ?: return true
        node.lock()
        node.left?.lock()
        node.right?.lock()
        val isValid = (node.left == null || node.left!!.value < node.value) &&
                (node.right == null || node.right!!.value > node.value) &&
                isValid(node.left) &&
                isValid(node.right)
        node.unlock()
        return isValid
    }

    fun isValid(): Boolean {
        if (root == null) return true
        root!!.lock()
        return isValid(root)
    }

    fun isEmpty(): Boolean {
        return root == null
    }

    



    companion object {
        fun newTree(): Tree {
            return Tree()
        }
    }
}
