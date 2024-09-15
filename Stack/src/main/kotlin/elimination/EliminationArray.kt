package elimination

import java.util.concurrent.TimeUnit
import kotlin.random.Random

class EliminationArray<T> (capacity: Int) {
    private val exchanger = Array(capacity) { LockFreeExchanger<T>() }

    companion object {
        const val DURATION = 2L;
    }


    fun visit(value: T, range: Int): T? {
        val slot = Random.nextInt(range)
        return exchanger[slot].exchange(value, DURATION, TimeUnit.MILLISECONDS)
    }
}

