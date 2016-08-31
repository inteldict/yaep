import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class EarleyParser extends AbstractEarley {

    private String[] words;

    public EarleyParser(Grammar grammar) {
        super(grammar);
    }

    protected void init(String[] words) {
        this.words = words;
        charts = new Chart[words.length + 1];
        for (int i = 0; i < charts.length; i++) {
            charts[i] = new Chart();
        }
    }

    @Override
    public IParseTreeGenerator buildTreeGenerator() {
        return new ParseTreeGenerator(this);
    }

    /**
     * Compares the next symbol in the input stream with the next symbol of the form (X → α • a β, i), if it matchses add (X → α a • β, i) to S(i+1).
     * @param state
     * @param i
     */
    public void scanner(State state, int i) {
        if (this.words[i].equals(state.getNextSymbol())) {
            charts[i + 1].addState(new State(state.rule, i, state.dot + 1));
        }
    }
}
