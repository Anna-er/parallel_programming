package treiberStackWithElimination;

import treiberStackWithElimination.Exchanger
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class EliminationArray<T>(private val capacity: Int) {
    companion object {
        const val DURATION: Long = 40
    }

    private val exchanger: Array<Exchanger<T?>> = Array(capacity) { Exchanger<T?>() }

    fun visit(value: T?): T? {
        val slot = Random.nextInt(capacity)
        return exchanger[slot].exchange(value, DURATION, TimeUnit.MILLISECONDS)
    }
}