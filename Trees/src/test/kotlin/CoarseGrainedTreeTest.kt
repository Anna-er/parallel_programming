import org.example.CoarseGrainedTree
import org.junit.jupiter.api.BeforeEach

class CoarseGrainedTreeTest : TreeTest() {

    @BeforeEach
    override fun setUp() {
        tree = CoarseGrainedTree()
    }
}