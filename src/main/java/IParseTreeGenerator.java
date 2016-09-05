import java.util.HashSet;
import java.util.List;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public interface IParseTreeGenerator {

    List<Node> parseTreesOnTime(ChartManager chartManager);

    List<Node> parseTrees(ChartManager chartManager);


}
