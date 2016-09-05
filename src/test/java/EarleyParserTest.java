import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
@RunWith(Theories.class)
public class EarleyParserTest {

    @DataPoint
    public static String[] tokens1 = {"Mary", "called", "Jan"};
    @DataPoint
    public static String[] tokens2 = {"Mary", "called", "Jan", "from", "Frankfurt"};

    private static SimpleGrammar grammar;
    private static EarleyParser parser;

    @BeforeClass
    public static void setUpClass() {
        grammar = new SimpleGrammar();
        grammar.readResource("grammar.txt");
        grammar.setNullable();
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
     * Check that recognition was successfull: last state in the chart
     * is equals to FINAL_STATE. Check also first state in the chart
     * @param charts
     */
    public void simpleParseTest(String[] tokens) {

        ChartManager chartManager = parser.parse(tokens);
        System.out.println(Chart.prettyPrint(String.join(" ", tokens), chartManager.getCharts()));
        //check chart size
        assertThat(chartManager.getCharts().length, equalTo(tokens.length + 1));
        assertTrue(1 == chartManager.initialStates().count());
        assertTrue(chartManager.isRecognized());
    }

    @Test
    public void simpleRecognizeTest() {
        String[] tokens = {"Mary", "called", "Jan"};
        //check chart size
        assertThat(parser.parse("Mary   called\tJan"), equalTo(parser.parse(tokens)));
    }
}
