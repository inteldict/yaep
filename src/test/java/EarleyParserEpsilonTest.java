import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
@RunWith(Theories.class)
public class EarleyParserEpsilonTest {

    public EarleyParser initParser(String path) {
        SimpleGrammar grammar = new SimpleGrammar();
        grammar.readResource(path);
        grammar.setNullable();
        return new EarleyParser(grammar);
    }

//    @Ignore("Parser need special technology to deal with left recursion\n")
    @Test
    public void leftRecursionTest() {
        EarleyParser parser = initParser("epsilon_a_left_recursion.txt");
        String[][] tokensArray = new String[][]{
                {"a"},
                {"a", "a"},
                {"a", "a", "a",},
                {"a", "a", "a", "a",},
                {"a", "a", "a", "a", "a"},
                {"a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"},};
        Stream.of(tokensArray).forEach(tokens -> {

            ChartManager chartManager = parser.parse(tokens);
            System.out.println(Chart.prettyPrint(String.join(" ", tokens), chartManager.getCharts()));
            //check chart size
            assertThat(chartManager.getCharts().length, equalTo((int) (tokens.length + 1)));

            assertTrue(chartManager.initialStates().count() > 0);
            assertTrue(chartManager.isRecognized());
        });
    }

//    @Ignore("Parser need special technology to deal with right recursion\n")
    @Test
    public void rightRecursionTest() {
        EarleyParser parser = initParser("epsilon_a_right_recursion.txt");
        String[][] tokensArray = new String[][]{
                {"a"},
                {"a", "a"},
                {"a", "a", "a",},
                {"a", "a", "a", "a",},
                {"a", "a", "a", "a", "a"},
                {"a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"},};
        Stream.of(tokensArray).forEach(tokens -> {

            ChartManager chartManager = parser.parse(tokens);
            System.out.println(Chart.prettyPrint(String.join(" ", tokens), chartManager.getCharts()));
            //check chart size
            assertThat(chartManager.getCharts().length, equalTo((int) (tokens.length + 1)));

            assertTrue(chartManager.initialStates().count() > 0);
            assertTrue(chartManager.isRecognized());
        });
    }

//    @Ignore("Parser need special technology to deal with left recursion\n")
    @Test
    public void epsilonABGrammarTest() {
        EarleyParser parser = initParser("epsilon_ab_grammar.txt");
        String[][] tokensArray = new String[][]{
                {"a", "a", "a", "b",},
                {"a", "a", "a", "a", "a", "b"},
                {"a", "a", "a", "a", "a", "a", "a", "a", "a", "b"},};
        Stream.of(tokensArray).forEach(tokens -> {

            ChartManager chartManager = parser.parse(tokens);
            System.out.println(Chart.prettyPrint(String.join(" ", tokens), chartManager.getCharts()));
            //check chart size
            assertThat(chartManager.getCharts().length, equalTo((int) (tokens.length + 1)));

            assertTrue(chartManager.initialStates().count() > 0);
            assertTrue(chartManager.isRecognized());
        });
    }
}
