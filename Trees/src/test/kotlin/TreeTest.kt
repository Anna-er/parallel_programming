import kotlinx.coroutines.*
import org.example.Tree
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.random.Random

abstract class TreeTest {
    protected lateinit var tree: Tree<Int, String> // Abstract tree instance for testing
    private val rnd = Random(0) // Random number generator for deterministic tests

    @BeforeEach
    abstract fun setUp() // Abstract setup method for initializing the tree before each test

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @ParameterizedTest
    @MethodSource("threadNumsProvider")
    fun addingValuesTest(threadsNum: Int) {
        // This test verifies that multiple threads can insert values into the tree concurrently
        // without data corruption. It runs the test with varying numbers of threads.

        val valuesToAddLists = List(threadsNum) { List(5000) { rnd.nextInt(5000) } }
        // Generate a list of random values to insert for each thread (5000 values per thread)

        runBlocking {
            // Launch threads to insert values concurrently
            valuesToAddLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { tree.insert(it, "hz") }
                    // Insert each value into the tree with a dummy value ("hz")
                }
            }
        }

        runBlocking {
            // Launch threads to search for the values that were inserted
            // Ensures all inserted values can be found in the tree.
            valuesToAddLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { Assertions.assertNotNull(tree.search(it)) }
                    // Assert that every inserted value can be found in the tree
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @ParameterizedTest
    @MethodSource("threadNumsProvider")
    fun deletingValuesTest(threadsNum: Int) {
        // This test checks that values can be deleted from the tree correctly
        // in a concurrent environment. It first inserts random values and then
        // deletes them, ensuring they are no longer present in the tree.

        val valuesToRemoveLists = List(threadsNum) { List(5000) { rnd.nextInt(5000) } }
        // Generate a list of random values to remove for each thread (5000 values per thread)

        val jobs = mutableListOf<Job>() // List to keep track of all insertion jobs
        runBlocking {
            // First, insert the values into the tree concurrently
            valuesToRemoveLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { tree.insert(it, "hz") }
                    // Insert each value into the tree with a dummy value ("hz")
                }.let {
                    jobs.add(it) // Keep track of each job to ensure they complete
                }
            }

            jobs.forEach { it.join() } // Ensure all insertion jobs are completed

            // Then, delete the values concurrently
            valuesToRemoveLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { tree.delete(it) }
                    // Delete each value from the tree
                }
            }
        }

        runBlocking {
            // Verify that all deleted values are no longer present in the tree
            valuesToRemoveLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { Assertions.assertNull(tree.search(it)) }
                    // Assert that each deleted value is no longer in the tree
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun threadNumsProvider(): List<Arguments> =
            // Provides different numbers of threads for the parameterized tests: 1, 2, and 4 threads
            listOf(Arguments.of(1), Arguments.of(2), Arguments.of(4))
    }
}
