package stack

import elimination.EliminationTreiberStack
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.LoggingLevel
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

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
}