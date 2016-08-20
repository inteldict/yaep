import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class Grammar {

    protected HashMap<NT, List<Rule>> rules = new HashMap<>();
    protected HashSet<NT> POS = new HashSet<>();
}
