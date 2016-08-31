import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class SimpleGrammarTest {

    @Test
    public void loadFileTest () {
        SimpleGrammar grammar = new SimpleGrammar("grammar/grammar.txt");
        // test grammar attributes
        Assert.assertEquals(grammar.rules.size(), grammar.nonterminals.size());
        Assert.assertEquals(7, grammar.nonterminals.size());

        // test terminals
        String[] expectedTerminals = {"Frankfurt", "Jan",  "Mary", "called", "from"};
        Arrays.sort(expectedTerminals);
        String[] actualTerminals = grammar.terminals.toArray(new String[0]);
        Arrays.sort(actualTerminals);
        Assert.assertArrayEquals("Terminal test:", expectedTerminals , actualTerminals);
    }

    @Test
    public void epsilonGrammarTest() {
        SimpleGrammar grammar = new SimpleGrammar("grammar/epsilon_paper_grammar.txt");
        // test nonterminals
        NT[] actualNonTerminals = grammar.nonterminals.toArray(new NT[0]);
        Arrays.sort(actualNonTerminals);
        NT[] expectedNonTerminals = {new NT("A"), new NT("E"), new NT("S"), new NT("ε")};
        Arrays.sort(expectedNonTerminals);
        Assert.assertArrayEquals("Nonterminal test:", expectedNonTerminals , actualNonTerminals);

        // test isNullable
        Assert.assertEquals(expectedNonTerminals.length - 1, Stream.of(actualNonTerminals).filter(NT::isNullable).count());

        // test terminals
        String[] expectedTerminals = {"a"};
        Assert.assertArrayEquals("Terminal test:", expectedTerminals , grammar.terminals.toArray(new String[0]));
    }

}
