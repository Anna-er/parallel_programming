import kotlin.random.Random
import org.junit.jupiter.api.Test
import kotlinx.coroutines.*

interface BST {
    suspend fun insert(x: Int)
    suspend fun find(x: Int): Boolean
    suspend fun remove(x: Int)

    fun isValid(): Boolean
    fun isEmpty(): Boolean
}

var seed: Long = 42
val rnd = Random(seed)
const val numberSize = 100000
val values = IntArray(numberSize) { rnd.nextInt() }

class CoarseGrainedTree : BST {
    private val tree = coarsegrained.Tree()

    override suspend fun insert(x: Int) = withContext(Dispatchers.Default) {
        tree.insert(x)
    }

    override suspend fun find(x: Int): Boolean = withContext(Dispatchers.Default) {
        tree.find(x)
    }

    override suspend fun remove(x: Int) = withContext(Dispatchers.Default) {
        tree.remove(x)
    }


    override fun isValid(): Boolean = tree.isValid()
    override fun isEmpty(): Boolean = tree.isEmpty()
}

class FineGrainedTree : BST {
    private val tree = finegrained.Tree()

    override suspend fun insert(x: Int) = withContext(Dispatchers.Default) {
        tree.insert(x)
    }

    override suspend fun find(x: Int): Boolean = withContext(Dispatchers.Default) {
        tree.find(x)
    }

    override suspend fun remove(x: Int) = withContext(Dispatchers.Default) {
        tree.remove(x)
    }

    override fun isValid(): Boolean = tree.isValid()
    override fun isEmpty(): Boolean = tree.isEmpty()
}

class OptimisticTree : BST {
    private val tree = optimistic.Tree()

    override suspend fun insert(x: Int) = withContext(Dispatchers.Default) {
        tree.insert(x)
    }

    override suspend fun find(x: Int): Boolean = withContext(Dispatchers.Default) {
        tree.find(x)
    }

    override suspend fun remove(x: Int) = withContext(Dispatchers.Default) {
        tree.remove(x)
    }

    override fun isValid(): Boolean = tree.isValid()
    override fun isEmpty(): Boolean = tree.isEmpty()
}

class TreeTests {
    @Test
    fun testBST() = runBlocking {
        val treeTypes = listOf(
            "coarse-grained" to ::CoarseGrainedTree,
            "optimistic" to ::OptimisticTree,
            "fine-grained" to ::FineGrainedTree
        )

        treeTypes.forEach { (name, treeConstructor) ->
            var tree = treeConstructor()

            runTest("Insert, Find and Remove test", tree, name) {
                for (v in values) {
                    launch { tree.insert(v) }
                }
                for (v in values) {
                    launch {
                        if (!tree.find(v)) {
                            println("FAILED|Find after Insert test|: $name tree doesn't contain $v.")
                        }
                    }
                }
                for (v in values) {
                    launch { tree.remove(v) }
                }
                if (!tree.isEmpty()) {
                    println("FAILED|Remove after Insert test|: $name tree is not empty.")
                }
            }
            tree = treeConstructor()
            runTest("Insert and Remove", tree, name) {
                for (i in values.indices) {
                    launch {
                        if (i % 2 == 0) {
                            tree.insert(values[i])
                        } else {
                            tree.remove(values[i])
                        }
                    }
                }
                if (!tree.isValid()) {
                    println("FAILED|Insert and Remove test|: $name tree is not valid.")
                }
            }
            tree = treeConstructor()
            runTest("Random operations", tree, name) {
                for (v in values) {
                    launch {
                        val rnd = Random(v)
                        val op = rnd.nextInt()
                        when {
                            op % 3 == 0 -> tree.insert(v)
                            op % 3 == 1 -> tree.find(v)
                            else -> tree.remove(v)
                        }
                    }
                }
                if (!tree.isValid()) {
                    println("FAILED|Random operations test|: $name tree is not valid.")
                }
            }
            tree = treeConstructor()
        }
    }

    private suspend fun runTest(name: String, tree: BST, treeName: String, testBlock: suspend CoroutineScope.() -> Unit) {
        println("Running $name test for $treeName tree")
        coroutineScope {
            testBlock()
        }
        println("COMPLETED")
    }
}
