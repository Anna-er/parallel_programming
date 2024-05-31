package optimistic

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
    private val treeLock = ReentrantLock()

    private fun lockTree() {
        treeLock.lock()
    }

    private fun unlockTree() {
        treeLock.unlock()
    }

    private fun validate(x: Int, node: Node?, parent: Node?): Boolean {
        if (node == null && parent == null) {
            return root == null
        }
        var curr = root
        var prev: Node? = null
        while (curr != null && curr.value != x && curr != node) {
            prev = curr
            curr = if (x < curr.value) curr.left else curr.right
        }
        return curr == node && prev == parent
    }

    private fun findHelper(x: Int): Pair<Node?, Node?> {
        while (true) {
            lockTree()
            if (root == null) {
                return Pair(null, null)
            }
            var curr = root
            var prev: Node? = null
            while (curr != null && curr.value != x) {
                val temp = prev
                prev = curr
                curr = if (x < curr.value) curr.left else curr.right
                if (temp == null) {
                    unlockTree()
                }
            }
            prev?.lock()
            curr?.lock()
            if (validate(x, curr, prev)) {
                return Pair(curr, prev)
            }
            curr?.unlock()
            prev?.unlock()
        }
    }

    fun insert(x: Int) {
        val (curr, prev) = findHelper(x)
        val node = Node(x)
        if (root == null) {
            unlockTree()
            root = node
            return
        }
        prev?.unlock() ?: unlockTree()
        curr?.unlock()
        if (curr != null) return
        if (x < prev!!.value) {
            prev.left = node
        } else {
            prev.right = node
        }
    }

    fun find(x: Int): Boolean {
        val (curr, prev) = findHelper(x)
        prev?.unlock() ?: unlockTree()
        curr?.unlock()
        return curr != null
    }

    fun remove(x: Int) {
        val (curr, prev) = findHelper(x)
        prev?.unlock() ?: unlockTree()
        if (curr == null) return
        curr.unlock()


        when {
            curr.left == null && curr.right == null -> {
                if (curr == root) {
                    root = null
                } else if (curr.value < prev!!.value) {
                    prev.left = null
                } else {
                    prev.right = null
                }
            }
            curr.left == null -> {
                if (curr == root) {
                    root = curr.right
                } else if (curr.value < prev!!.value) {
                    prev.left = curr.right
                } else {
                    prev.right = curr.right
                }
            }
            curr.right == null -> {
                if (curr == root) {
                    root = curr.left
                } else if (curr.value < prev!!.value) {
                    prev.left = curr.left
                } else {
                    prev.right = curr.left
                }
            }
            else -> {
                curr.right!!.lock()
                var succParent = curr
                var succ = curr.right
                while (succ!!.left != null) {
                    val temp = succParent
                    succParent = succ
                    succ.left!!.lock()
                    succ = succ.left
                    if (temp != curr) {
                        temp!!.unlock()
                    }
                }
                succ.unlock()
                if (succParent != curr) {
                    succParent?.left = succ.right
                    succParent?.unlock()
                } else {
                    succParent.right = succ.right
                }
                curr.value = succ.value
            }
        }
    }

    private fun isValid(node: Node?): Boolean {
        if (node == null) return true
        if (node.left != null && node.left!!.value >= node.value) return false
        if (node.right != null && node.right!!.value <= node.value) return false
        return isValid(node.left) && isValid(node.right)
    }

    fun isValid(): Boolean {
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
