import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class PermutationEarleyParser extends AbstractEarley {

    private HashMap<CharSequence, Integer> wordsMap = new HashMap<>();

    public PermutationEarleyParser(Grammar grammar) {
        super(grammar);
    }

    protected void init(String[] words) {
        if (!wordsMap.isEmpty()) {
            wordsMap.clear();
        }
        Integer index;
        charts = new Chart[words.length + 1];
        for (int i = 0; i < words.length; i++) {
            charts[i] = new Chart();
            if ((index = wordsMap.get(words[i])) == null) {
                index = 1;
            } else {
                index++;
            }
            wordsMap.put(words[i], index);
        }
        charts[words.length] = new Chart();
    }

    @Override
    public IParseTreeGenerator buildTreeGenerator() {
        return new PermutationParseTreeGenerator(this);
    }

    /**
     * Compares the next symbol in the input stream with the next symbol of the form (X → α • a β, i), if it matchses add (X → α a • β, i) to S(i+1).
     *
     * @param state
     * @param i
     */
    public void scanner(State state, int i) {
        // 1st: check if symbol in input
        if (wordsMap.containsKey(state.getNextSymbol())) {
            charts[i + 1].addState(new State(state.rule, i, state.dot + 1));
        }
    }

    public HashMap<CharSequence, Integer> getWordsMap() {
        return wordsMap;
    }
}
