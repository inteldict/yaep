import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class Grammar {

    protected static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    protected final HashMap<NT, List<Rule>> rules = new HashMap<>();
    protected final HashSet<CharSequence> terminals = new HashSet<>();
}
