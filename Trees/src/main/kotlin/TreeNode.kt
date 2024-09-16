package org.example

import kotlinx.coroutines.sync.Mutex

data class TreeNode<K : Comparable<K>, V>(
    var key: K,
    var value: V
) {
    var left: TreeNode<K, V>? = null
    var right: TreeNode<K, V>? = null

    private val lock = Mutex()

    suspend fun lock() = lock.lock()
    fun unlock() = lock.unlock()
}
