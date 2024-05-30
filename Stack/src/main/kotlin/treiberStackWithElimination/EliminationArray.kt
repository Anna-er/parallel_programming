package treiberStackWithElimination

import kotlin.random.Random

const val EXCHANGE_ATTEMPTS = 10

class EliminationArray<T>(private val capacity: Int) {
    private val exchanger = Array(capacity) { _ -> Exchanger<T>(EXCHANGE_ATTEMPTS) }

    fun visit(value: T?): Result<T?> {
        val slot = Random.nextInt(capacity)

        return exchanger[slot].exchange(value)
    }
}
