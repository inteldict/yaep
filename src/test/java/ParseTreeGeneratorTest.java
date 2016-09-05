import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.runner.RunWith;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
@RunWith(Theories.class)
public class ParseTreeGeneratorTest {

    @DataPoint
    public static Map.Entry<Integer, List<String>> sentence1 = new AbstractMap.SimpleEntry<>(1, Arrays.asList(new String[]{"Mary", "called", "Jan"}));

    @DataPoint
    public static Map.Entry<Integer, List<String>> sentence2 = new AbstractMap.SimpleEntry<>(2, Arrays.asList(new String[]{"Mary", "called", "Jan", "from", "Frankfurt"}));

    private static SimpleGrammar grammar;
    private static EarleyParser parser;

    @BeforeClass
    public static void setUpClass() {
        grammar = new SimpleGrammar();
        grammar.readResource("grammar.txt");
        grammar.setNullable();
    }

    @AfterClass
    public static void tearDownClass() {
        grammar = null;
    }

    @Before
    public void setUp() {
        parser = new EarleyParser(grammar);
    }

    @After
    public void tearDown() {
        parser = null;
    }

    @Theory
    /**
     * Check that recognition was successfull: last contains at least one "final state".
     * Check also first state in the chart
     * @param charts
     */
    public void simpleParseTest(Map.Entry<Integer, List<String>> sentence) {
        ChartManager chartManager = parser.parse(sentence.getValue().toArray(new String[0]));
        IParseTreeGenerator parseTreeGenerator = parser.buildTreeGenerator();
        List<Node> trees = parseTreeGenerator.parseTreesOnTime(chartManager);
        //check chart size
        assertThat("Number of trees:", trees.size(), equalTo(sentence.getKey()));
    }

}
