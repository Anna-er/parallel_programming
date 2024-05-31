package sequential

class Node(var value: Int) {
    var left: Node? = null
    var right: Node? = null
}

class Tree {
    private var root: Node? = null

    private fun findHelper(x: Int): Pair<Node?, Node?> {
        var curr = root
        var prev: Node? = null
        while (curr != null && curr.value != x) {
            prev = curr
            curr = if (x < curr.value) curr.left else curr.right
        }
        return Pair(curr, prev)
    }

    fun insert(x: Int) {
        val (curr, prev) = findHelper(x)
        if (root == null) {
            root = Node(x)
            return
        }
        if (curr != null) return
        val node = Node(x)
        if (x < prev!!.value) {
            prev.left = node
        } else {
            prev.right = node
        }
    }

    fun find(x: Int): Boolean {
        val (curr, _) = findHelper(x)
        return curr != null
    }

    fun remove(x: Int) {
        val (curr, prev) = findHelper(x)
        if (curr == null) return

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
                var succParent = curr
                var succ = curr.right
                while (succ!!.left != null) {
                    succParent = succ
                    succ = succ.left
                }
                if (succParent != curr) {
                    succParent?.left = succ.right
                } else {
                    succParent.right = succ.right
                }
                curr.value = succ.value
            }
        }
    }

    private fun inOrderPrint(node: Node?) {
        if (node == null) return
        inOrderPrint(node.left)
        print("${node.value} ")
        inOrderPrint(node.right)
    }

    fun inOrderPrint() {
        inOrderPrint(root)
        println()
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
