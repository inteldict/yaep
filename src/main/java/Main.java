import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class Main {

    public static final String GRAMMAR_OPTION = "grammar";
    public static final String PERMUTATION_OPTION = "permutation";

    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

//    public static void main(String[] args) {
//
//        String[] sentence1 = {"Mary", "called", "Jan"};
//        String[] sentence2 = {"Mary", "called", "Jan", "from", "Frankfurt"};
//        String[] sentence3 = {"a", "b"};
//        String[] sentence4 = {"x"};
//        String[] sentence5 = {"a"};
////        SimpleGrammar grammar = new SimpleGrammar("epsilon_paper_grammar.txt");
//        SimpleGrammar grammar = new SimpleGrammar("grammar.txt");
//        EarleyPermutationParser parser = new EarleyPermutationParser(grammar);
//        parseTrees(sentence1, parser);
//        parseTrees(sentence2, parser);
////        parseTrees(sentence3, parser);
////        parseTrees(sentence4, parser);
////        parseTrees(sentence5, parser);
//    }


    public static void main(String[] args) throws Exception {

        Options options = new Options();
        options.addOption("p", PERMUTATION_OPTION, false, "Permutation of the input string");

        Option input = new Option("g", GRAMMAR_OPTION, true, "grammar file path");
        input.setRequired(true);
        options.addOption(input);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
//            System.getProperty("sun.java.command")
            formatter.printHelp("YAEP  [options] -g file input [[input2 [input3] ...]]", options);
            System.exit(1);
            return;
        }

        String grammarFilePath = cmd.getOptionValue(GRAMMAR_OPTION);
        SimpleGrammar grammar = new SimpleGrammar(grammarFilePath);

        IEarley earleyParser = cmd.hasOption(PERMUTATION_OPTION) ? new PermutationEarleyParser(grammar) : new EarleyParser(grammar);

        // Read input sentences from args and/or from pipe
        Stream<String> inputStream = null;

        //Check if input was given through pipe
        BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        List<String> argsList;
        if (buf.ready()) {
            inputStream = buf.lines();
        } else if (!(argsList = cmd.getArgList()).isEmpty()) {
            inputStream = argsList.stream();
        } else {
            log.severe("No input was given");
            System.exit(1);
        }

        inputStream.
                forEach(sentence -> {
                    Chart[] charts = recognize(sentence, earleyParser);
                    if (charts != null) {
                        buildTrees(earleyParser.buildTreeGenerator());
                    }
                });
    }

    public static Chart[] recognize(String input, IEarley parser) {
        if (input == null | input.isEmpty()) {
            return null;
        }
        String[] tokens = input.split("\\s+");
        Chart[] charts = parser.parseOnTime(tokens);
        log.info(() -> {
            StringBuilder chartOutput = new StringBuilder("Charts produced by the sentence: " + input);
            for (int i = 0; i < charts.length; i++) {
                chartOutput.append("\nChart " + i + ":\n" + charts[i].toString(i));
            }
            return chartOutput.toString();
        });
        return charts;
    }

    public static List<Node> buildTrees(IParseTreeGenerator parseTreeGenerator) {
        List<Node> trees = parseTreeGenerator.parseTreesOnTime();
        log.info(() -> {
            StringBuilder treeOutput = new StringBuilder();
            for (Node tree : trees) {
                treeOutput.append(tree.prettyPrint(0)).append('\n');
            }
            treeOutput.append("Number of trees: ").append(trees.size());
            return treeOutput.toString();
        });
        return trees;
    }

}
