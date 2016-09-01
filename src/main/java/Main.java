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
                    Chart[] charts = earleyParser.recognize(sentence);
                    if (charts != null) {
                        log.info(() -> "Charts produced by the sentence: " + sentence + "\n" + Chart.prettyPrint(charts));
                        buildTrees(earleyParser.buildTreeGenerator());
                    }
                });
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
