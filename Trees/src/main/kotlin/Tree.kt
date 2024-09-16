package org.example

abstract class Tree<K : Comparable<K>, V> {
    private var root: TreeNode<K, V>? = null

    abstract suspend fun insert(key: K, value: V)
    abstract suspend fun search(key: K): V?
    abstract suspend fun delete(key: K)
    fun getKeys(): List<K> {
        fun inOrderTraversal(node: TreeNode<K, V>?, keys: MutableList<K>) {
            node ?: return
            inOrderTraversal(node.left, keys)
            keys.add(node.key)
            inOrderTraversal(node.right, keys)
        }
        val keys = mutableListOf<K>()
        inOrderTraversal(root, keys)
        return keys
    }
}
