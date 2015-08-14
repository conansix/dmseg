package xyz.dowenliu.npl.dmseg.segment;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.dowenliu.npl.dmseg.core.Segmenter;
import xyz.dowenliu.npl.dmseg.core.Token;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * create at 15-4-30
 *
 * @author liufl
 * @since 1.0.0
 */
public class DefaultSegmenterFactoryTest {
    Logger logger = LoggerFactory.getLogger(getClass());
    private SegmenterFactory segmenterFactory;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        this.segmenterFactory = new DefaultSegmenterFactory(
                resource -> new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(resource)),
                "ik.txt");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreate() {
        Segmenter segmenter = segmenterFactory.create();
        assertNotNull(segmenter);
        int c = 0;
        int p = 0;
        Iterator<Token> tokenIterator = null;
        try {
            StringReader reader;
            reader = new StringReader("Dmseg是一个优秀的流式中文分词器。2015年于大北京");

            tokenIterator = segmenter.indexTokens(reader);
//            tokenIterator = segmenter.queryTokens(reader);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        assertNotNull(tokenIterator);
        while (tokenIterator.hasNext()) {
            Token token = tokenIterator.next();
            c++;
            p += token.getPositionIncrement();
            logger.info(String.format("%2d", p) + " : " + token.getValue() + " [" + token.getOffset() + ',' + token
                    .getEnd() + ')' + ' ' + token.getType().name());
        }
        assertTrue(c > 0);
    }
}
