import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class SimpleGrammarTest {

    private SimpleGrammar grammar;

    @Before
    public void setUp() {
        grammar = new SimpleGrammar();
    }

    @After
    public void tearDown()  {
        grammar = null;
    }

    @Test
    public void loadFileTest () {
        grammar.readResource("grammar.txt");
        grammar.setNullable();
        // test grammar attributes
        assertEquals(7, grammar.rules.size());
        // test terminals
        CharSequence[] expectedTerminals = {"Frankfurt", "Jan", "Mary", "called", "from"};
        Arrays.sort(expectedTerminals);
        CharSequence[] actualTerminals = grammar.terminals.toArray(new CharSequence[0]);
        Arrays.sort(actualTerminals);
        assertArrayEquals("Terminal test:", expectedTerminals , actualTerminals);
    }

    @Test
    public void epsilonGrammarTest() {
        grammar.readResource("epsilon_paper_grammar.txt");
        grammar.setNullable();

        // test nonterminals
        NT[] actualNonTerminals = grammar.rules.keySet().toArray(new NT[0]);
        Arrays.sort(actualNonTerminals);

        NT[] expectedNonTerminals = {new NT("A"), new NT("E"), new NT("S"), SimpleGrammar.EPSILON};
        Arrays.sort(expectedNonTerminals);
        assertArrayEquals("Nonterminal test:", expectedNonTerminals , actualNonTerminals);

        // test isNullable

        assertEquals(expectedNonTerminals.length - 1, Stream.of(actualNonTerminals).filter(NT::isNullable).count());

        // test terminals
        String[] expectedTerminals = {"a"};
        assertArrayEquals("Terminal test:", expectedTerminals , grammar.terminals.toArray(new String[0]));
    }

}
