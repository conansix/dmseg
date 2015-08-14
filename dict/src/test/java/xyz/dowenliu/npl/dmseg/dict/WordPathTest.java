package xyz.dowenliu.npl.dmseg.dict;

import org.junit.*;

import java.util.Deque;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author liufl
 * @since 1.0.0
 */
public class WordPathTest {
    private WordPath<MockWordPath> parent;
    private WordPath<MockWordPath> path;
    private WordPath<MockWordPath> samePath;
    private WordPath<MockWordPath> diffPath;

    private class MockWordPath extends WordPath<MockWordPath> {
        public MockWordPath(WordPath parentPath, char fork) {
            super(parentPath, fork);
        }

        @Override
        public Map<Character, MockWordPath> getBranches() {
            throw new UnsupportedOperationException();
        }
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        this.parent = new MockWordPath(null, 't');
        this.path = new MockWordPath(new MockWordPath(new MockWordPath(parent, 'e'), 's'), 't');
        this.path.wordFinish("v");
        this.samePath = new MockWordPath(new MockWordPath(new MockWordPath(parent, 'e'), 's'), 't');
        this.samePath.wordFinish();
        this.diffPath = new MockWordPath(new MockWordPath(new MockWordPath(parent, 'e'), 'x'), 't');
        this.diffPath.wordFinish();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testToString() {
        assertEquals("test", this.path.toString());
    }

    @Test
    public void testIsFinishWord() {
        assertTrue(this.path.isFinishWord());
        assertFalse(this.parent.isFinishWord());
    }

    @Test
    public void testGetWord() {
        assertNull(this.parent.getWord());
        Word word = this.path.getWord();
        assertEquals("test", word.getValue());
        assertTrue(this.path == word.getPath());
        assertTrue(word.getSpeeches().contains("v") && word.getSpeeches().size() == 1);
    }

    @Test
    public void testEquals_HashCode() {
        assertEquals("test".hashCode(), this.path.hashCode());
        assertEquals(this.path.hashCode(), this.samePath.hashCode());
        assertNotEquals("test", this.path);
        assertEquals(this.path, this.samePath);
        assertNotEquals(this.path, this.diffPath);
    }

    @Test
    public void testGetPathQueue() {
        Deque<WordPath> pathDeque = this.path.getPathQueue();
        assertEquals(4, pathDeque.size());
        assertEquals(this.parent, pathDeque.getFirst());
        assertTrue(pathDeque.pop() == pathDeque.pop().getParentPath());
    }
}
