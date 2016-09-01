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

        Chart[] charts = parser.parse(tokens);
        //check chart size
        assertThat(charts.length, equalTo(tokens.length + 1));

        State firstState = charts[0].getState(0);
        assertThat(firstState, equalTo(AbstractEarley.INIT_STATE));

        List<State> states = charts[charts.length - 1].states;
        State lastState = states.get(states.size() - 1);
        assertThat(lastState, equalTo(AbstractEarley.FINAL_STATE));
    }

    @Test
    public void simpleRecognizeTest() {
        String[] tokens = {"Mary", "called", "Jan"};
        //check chart size
        assertThat(parser.recognize("Mary   called\tJan"), equalTo(parser.parse(tokens)));
    }
}
