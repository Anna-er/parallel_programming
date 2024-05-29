package mpp.stack

import intStackSequential.IntStackSequential
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import kotlinx.coroutines.*
import mpp.stackWithElimination.TreiberStackWithElimination
import org.jetbrains.kotlinx.lincheck.*
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.*
import org.jetbrains.kotlinx.lincheck.strategy.stress.*
import org.jetbrains.kotlinx.lincheck.verifier.VerifierState
import org.junit.*
import java.io.File
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class TreiberStackTest {
    private val q = TreiberStack<Int>()

    @Operation
    fun push(x: Int): Unit = q.push(x)

    @Operation
    fun pop(): Int? = q.pop()

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

    @Test
    fun performanceTest() {
        val threads = 10
        val iterations = 1000000
        val stack = TreiberStack<Int>()

        var executionTime = 0L
        repeat(10) {
            val jobs = mutableListOf<Job>()
            val time = measureTimeMillis {
                runBlocking {
                    repeat(threads) { curThread ->
                        jobs.add(launch(newSingleThreadContext(curThread.toString() + "OP")) {
                            repeat(iterations) {
                                if (curThread % 2 == 0) {
                                    stack.push(Random.nextInt(1_000_000_000))
                                } else {
                                    stack.pop()
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

        println("Execution time of TreiberStack: $executionTime milliseconds")
    }
}
