package stack

import elimination.EliminationTreiberStack
import kotlinx.coroutines.*
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.LoggingLevel
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class EliminationTreiberStackTest {
    private val stack =
        EliminationTreiberStack<Int>(10)

    @Operation
    fun top() = stack.top()

    @Operation
    fun push(value: Int) = stack.push(value)

    @Operation
    fun pop() = stack.pop()

    @Test
    fun stressTest() = StressOptions()
        .iterations(40)
        .invocationsPerIteration(200)
        .threads(3)
        .actorsPerThread(3)
        .logLevel(LoggingLevel.INFO)
        .check(this::class)


    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @Test
    fun performanceTest() {
        val iterations = 1000000
        val stack = EliminationTreiberStack<Int>(10)
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

            println("Execution time of Elimination Treiber Stack for $threads threads = $executionTime ms")
        }
    }
}