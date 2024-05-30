package treiberStackWithElimination

import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicStampedReference


class Exchanger<T>(private val exchangeAttempts: Int) {
    companion object {
        private const val EMPTY = 0
        private const val WAITING = 1
        private const val BUSY = 2
    }


    private val slot = AtomicStampedReference<T>(null, EMPTY)

    fun exchange(item: T?): Result<T?> {
        val stampHolder = IntArray(1) { EMPTY }

        repeat(exchangeAttempts) {
            val curItem = slot.get(stampHolder)
            val stamp = stampHolder[0]
            when (stamp) {
                EMPTY -> {
                    if (slot.compareAndSet(curItem, item, EMPTY, WAITING)) {
                        repeat(exchangeAttempts) {
                            val newItem = slot.get(stampHolder)
                            if (stampHolder[0] == BUSY) {
                                slot.set(null, EMPTY)
                                return Result.success(newItem)
                            }
                        }
                        if (slot.compareAndSet(item, null, WAITING, EMPTY)) {
                            return Result.failure(TimeoutException())
                        }
                        val newItem = slot.get(stampHolder)
                        slot.set(null, EMPTY)
                        return Result.success(newItem)
                    }
                }

                WAITING -> {
                    if (slot.compareAndSet(curItem, item, WAITING, BUSY)) {
                        return Result.success(curItem)
                    }
                }
            }
        }
        return Result.failure(TimeoutException())
    }
}
