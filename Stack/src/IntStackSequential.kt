package intStackSequential

class IntStackSequential {
    private val q = ArrayDeque<Int>()

    fun push(x: Int) {
        q.addLast(x)
    }

    fun pop(): Int? = q.removeLastOrNull()

}

