import java.util.List;
import java.util.logging.Logger;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class Main {

    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {

        String[] sentence1 = {"Mary", "called", "Jan"};
        String[] sentence2 = {"Mary", "called", "Jan", "from", "Frankfurt"};
        SimpleGrammar grammar = new SimpleGrammar("grammar.txt");
        EarleyPermutationParser parser = new EarleyPermutationParser(grammar);
        test(sentence1, parser);
        test(sentence2, parser);
    }

    static void test(String[] sent, EarleyPermutationParser parser) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < sent.length - 1; i++)
            out.append(sent[i] + " ");
        out.append(sent[sent.length - 1] + ".");
        String sentence = out.toString();
        Chart[] charts = parser.parseOnTime(sent);
        log.info(() -> {
            StringBuilder chartOutput = new StringBuilder("Charts produced by the sentence: " + sentence);
            for (int i = 0; i < charts.length; i++) {
                chartOutput.append("\nChart " + i + ":\n" + charts[i].toString(i));
            }
            return chartOutput.toString();
        });
        ParseTreeGenerator parseTreeGenerator = new ParseTreeGenerator(charts, parser.getWordsMap());
        List<Node> trees = parseTreeGenerator.parseTreesOnTime();
        log.info(() -> {
            StringBuilder treeOutput = new StringBuilder();
            for (Node tree : trees) {
                treeOutput.append(tree.prettyPrint(0)).append('\n');
            }
            treeOutput.append("Number of trees: ").append(trees.size());
            return treeOutput.toString();
        });
    }
}
