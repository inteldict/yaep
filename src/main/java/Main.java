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
        EarleyParser parser = new EarleyParser(grammar);
        test(sentence1, parser);
        test(sentence2, parser);
    }

    static void test(String[] sent, EarleyParser parser) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < sent.length - 1; i++)
            out.append(sent[i] + " ");
        out.append(sent[sent.length - 1] + ".");
        String sentence = out.toString();
        Chart[] charts = parser.parseOnTime(sent);
        log.info(() -> {
            StringBuilder chartOutput = new StringBuilder("Charts produced by the sentence: " + sentence);
            for (int i = 0; i < charts.length; i++) {
                chartOutput.append("\nChart " + i + ":\n" + charts[i]);
            }
            return chartOutput.toString();
        });
    }
}
