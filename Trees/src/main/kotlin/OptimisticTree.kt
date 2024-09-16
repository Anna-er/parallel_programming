package org.example

class OptimisticTree<K : Comparable<K>, V>: Tree<K, V>() {
    private var root: TreeNode<K, V>? = null

    private suspend fun searchAux(key: K): Pair<TreeNode<K, V>?, TreeNode<K, V>?> {
        while (true) {
            var parent: TreeNode<K, V>? = null
            var current: TreeNode<K, V>? = root

            while (current != null && current.key != key) {
                parent = current
                current = if (key < current.key) current.left else current.right
            }

            parent?.lock()
            current?.lock()

            // current is
            // 1. not found
            // 2. root
            if (current == null || parent == null) {
                return current to parent
            }

            var validationParent: TreeNode<K, V>? = root
            while (parent != validationParent) {
                val validationParentKey = checkNotNull(validationParent?.key)
                validationParent = if (validationParentKey < parent.key) validationParent?.right else validationParent?.left
                if (validationParent == null) {
                    // again
                    break
                }
            }

            if (current == validationParent?.left || current == validationParent?.right) {
                return current to parent
            } else {
                // retry
                current.unlock()
                parent.unlock()
//                searchAux(key)
            }
        }
    }

    override suspend fun search(key: K): V? {
        val (node, parent) = searchAux(key)
        val value = node?.value
        node?.unlock()
        parent?.unlock()
        return value
    }

    override suspend fun insert(key: K, value: V) {
        val (node, parent) = searchAux(key)
        when {
            parent == null -> {
                if (root == null) {
                    root = TreeNode(key, value)
                } else {
                    root!!.value = value
                }
            }
            node == null -> {
                val newNode = TreeNode(key, value)
                if (key < parent.key) {
                    parent.left = newNode
                } else {
                    parent.right = newNode
                }
            }
            else -> {
                node.value = value
            }
        }
        node?.unlock()
        parent?.unlock()
    }


    override suspend fun delete(key: K) {
        val (node, parent) = searchAux(key)
        deleteRec(node, parent)
    }

    private suspend fun deleteRec(node: TreeNode<K, V>?, parent: TreeNode<K, V>?): TreeNode<K, V>? {
        when {
            node == null -> {
                parent?.unlock()
                return parent
            }

            // No children and 1 children case combined
            node.left == null || node.right == null -> {
                val child = node.left ?: node.right

                if (parent == null) {
                    root = child
                } else {
                    if (node == parent.left) {
                        parent.left = child
                    } else {
                        parent.right = child
                    }
                }
                node.unlock()
                parent?.unlock()
            }

            else -> {
                node.right?.lock()
                node.left?.lock()
                val right = node.right!!
                val left = node.left!!
//                right.lock()
//                left.lock()
                val (rightmostNode, rightmostNodeParent) = rightmostNode(left)
                rightmostNode.right = right


                if (parent == null) {
                    root = left
                }
                else if (node == parent.left) {
                    parent.left = left
                } else {
                    parent.right = left
                }

                rightmostNode.unlock()
                rightmostNodeParent?.unlock()

                right.unlock()
                node.unlock()
                parent?.unlock()
            }
        }
        return parent
    }

    private suspend fun rightmostNode(node: TreeNode<K, V>): Pair<TreeNode<K, V>, TreeNode<K, V>?> {
        var current = node
        var parent: TreeNode<K, V>? = null

        while (current.right != null) {
            val next = current.right
            next?.lock()

            parent?.unlock()
            parent = current
            current = checkNotNull(next)
        }

        return current to parent

    }
}
