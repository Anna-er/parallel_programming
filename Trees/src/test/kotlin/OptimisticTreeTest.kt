import org.example.OptimisticTree
import org.junit.jupiter.api.BeforeEach

class OptimisticTreeTest : TreeTest() {

    @BeforeEach
    override fun setUp() {
        tree = OptimisticTree()
    }
}