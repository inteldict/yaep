/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class Main {

    public static void main(String[] args) {

        String[] sentence1 = {"John", "called", "Mary"};
        String[] sentence2 = {"John", "called", "Mary", "from", "Denver"};
        SimpleGrammar grammar = new SimpleGrammar("grammar.txt");
        EarleyParser parser = new EarleyParser(grammar);
        test(sentence1, parser);
//        test(sentence2, parser);
    }

    static void test(String[] sent, EarleyParser parser) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < sent.length - 1; i++)
            out.append(sent[i] + " ");
        out.append(sent[sent.length - 1] + ".");
        String sentence = out.toString();
        System.out.println(
                "\nSentence: \"" + sentence + "\"");
        boolean successful =
                parser.parseSentence(sent);
        System.out.println(
                "Parse Successful:" + successful);
        Chart[] charts = parser.getCharts();
        System.out.println("");
        System.out.println("Charts produced by the sentence: " + sentence);
        for (int i = 0; i < charts.length; i++) {
            System.out.println("Chart " + i + ":");
            System.out.println(charts[i]);
        }
    }

}