import intStackSequential.IntStackSequential
import treiberStackWithElimination.TreiberStackWithElimination
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis
import org.jetbrains.kotlinx.lincheck.LoggingLevel
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

@Suppress("UNUSED")
class TreiberStackWithEliminationTest {
    private val stack1 = TreiberStackWithElimination<Int>()

    @Operation
    fun push(value: Int) = stack1.push(value)

    @Operation
    fun pop() = stack1.pop()

    @Operation
    fun top() = stack1.top()

    @Test
    fun stressTest() =
        StressOptions()
            .iterations(10)
            .invocationsPerIteration(50_000)
            .threads(3)
            .actorsPerThread(3)
            .sequentialSpecification(IntStackSequential::class.java)
            .logLevel(LoggingLevel.INFO)
            .check(this::class.java)

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @Test
    fun performanceTest() {
        val iterations = 1000000
        val stack = TreiberStackWithElimination<Int>()
        for (threads in mutableListOf(1, 2, 4, 8, 12)) {
            var executionTime = 0L
            repeat(10) {
                val jobs = mutableListOf<Job>()
                val time = measureTimeMillis {
                    runBlocking {
                        repeat(threads) { i ->
                            jobs.add(launch(newSingleThreadContext(i.toString())) {
                                repeat(iterations) {
                                    val j = Random.nextInt(100)
                                    if (j % 3 == 0) {
                                        stack.push(Random.nextInt(100))
                                    }
                                    if (j % 3 == 1) {
                                        stack.pop()
                                    }
                                    if (j % 3 == 2) {
                                        stack.top()
                                    }
                                }
                            })
                        }
                        jobs.joinAll()
                    }
                }
                executionTime += time
            }
            executionTime /= 10

            println("Execution time of TreiberStackWithElimination for $threads threads = $executionTime ms")
        }
    }
}
