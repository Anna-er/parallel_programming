package elimination

import java.util.concurrent.TimeoutException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicStampedReference

// Exchanger is a lock-free object that permits two threads
// to exchange values, within a time limit.

class LockFreeExchanger<T> {
    companion object {
        const val EMPTY = 0
        const val WAITING = 1
        const val BUSY = 2
    }

    private val slot = AtomicStampedReference<T?>(null, EMPTY)

    @Throws(TimeoutException::class)
    fun exchange(myItem: T?, timeout: Long, unit: TimeUnit): T? {
        val nanos = unit.toNanos(timeout)
        val timeBound = System.nanoTime() + nanos
        val stampHolder = intArrayOf(EMPTY)

        while (true) {
            if (System.nanoTime() > timeBound) throw TimeoutException()
            var yrItem: T? = slot.get(stampHolder)
            val stamp = stampHolder[0]

            when (stamp) {
                EMPTY -> {
                    if (slot.compareAndSet(yrItem, myItem, EMPTY, WAITING)) {
                        while (System.nanoTime() < timeBound) {
                            yrItem = slot.get(stampHolder)
                            if (stampHolder[0] == BUSY) {
                                slot.set(null, EMPTY)
                                return yrItem
                            }
                        }
                        if (slot.compareAndSet(myItem, null, WAITING, EMPTY)) {
                            throw TimeoutException()
                        } else {
                            yrItem = slot.get(stampHolder)
                            slot.set(null, EMPTY)
                            return yrItem
                        }
                    }
                }

                WAITING -> {
                    if (slot.compareAndSet(yrItem, myItem, WAITING, BUSY)) return yrItem
                }

                BUSY -> {
                    // just continue the loop
                }
                else -> throw IllegalStateException("Illegal State")
            }
        }
    }
}

