import org.example.FineGrainedTree
import org.junit.jupiter.api.BeforeEach

class FineGrainedTreeTest : TreeTest() {

    @BeforeEach
    override fun setUp() {
        tree = FineGrainedTree()
    }
}