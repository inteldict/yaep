import java.util.HashSet;
import java.util.List;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public interface IParseTreeGenerator {

    List<Node> parseTreesOnTime();

    List<Node> parseTrees();

    /**
     * Method, that used by parseTrees to generate subtrees.
     * @param state A completed state from Chart
     * @param parentStates States from Chart, that were influenced by @param state during parsing
     * @return all possible trees alternatives
     */
    List<Node> buildTrees(ExtendedState state, HashSet<ExtendedState> parentStates);
}
