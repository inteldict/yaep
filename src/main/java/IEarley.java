import java.util.List;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public interface IEarley {

    void predictor(State state, int i);

    void scanner(State state, int i);

    void completer(State state, int i);

    /**
     * lowlewel function, that does actual parisng
     * @param words tokenized input
     * @return
     */
    ChartManager parse(String[] words);

    ChartManager parseOnTime(String[] words);

    IParseTreeGenerator buildTreeGenerator();

    /**
     * Tokenize input and parse
     * @param input sentence to be parsed
     * @return charts
     */
    ChartManager parse(String input);
}
