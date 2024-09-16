import kotlinx.coroutines.*
import org.example.Tree
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.random.Random

abstract class TreeTest {
    protected lateinit var tree: Tree<Int, String>
    private val rnd = Random(0)

    @BeforeEach
    abstract fun setUp()

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @ParameterizedTest
    @MethodSource("threadNumsProvider")
    fun addingValuesTest(threadsNum: Int) {
        val valuesToAddLists = List(threadsNum) { List(5000) { rnd.nextInt(5000) } }
        runBlocking {
            valuesToAddLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { tree.insert(it, "hz") }
                }
            }
        }
        runBlocking {
            valuesToAddLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { Assertions.assertNotNull(tree.search(it)) }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @ParameterizedTest
    @MethodSource("threadNumsProvider")
    fun deletingValuesTest(threadsNum: Int) {
        val valuesToRemoveLists = List(threadsNum) { List(5000 ) { rnd.nextInt(5000) } }
        valuesToRemoveLists.forEach {
            println(it)
        }
        val jobs = mutableListOf<Job>()
        runBlocking {
            valuesToRemoveLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { tree.insert(it, "hz") }
                }.let {
                    jobs.add(it)
                }
            }

            jobs.forEach {
                it.join()
            }
            valuesToRemoveLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { tree.delete(it) }
                }
            }
        }
        runBlocking {
            valuesToRemoveLists.forEachIndexed { id, list ->
                launch(newSingleThreadContext("Thread$id")) {
                    list.forEach { Assertions.assertNull(tree.search(it)) }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun threadNumsProvider(): List<Arguments> =
            listOf(Arguments.of(1), Arguments.of(2), Arguments.of(4))
    }
}