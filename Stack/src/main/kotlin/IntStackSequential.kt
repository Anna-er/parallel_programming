package intStackSequential

class IntStackSequential {
    private val q = mutableListOf<Int>()

    fun top(): Int? = q.lastOrNull()

    fun push(x: Int) {
        q.add(x)
    }

    fun pop(): Int? = q.removeLastOrNull()
}
