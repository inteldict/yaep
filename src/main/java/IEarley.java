import java.util.List;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public interface IEarley {

    void predictor(State state, int i);

    void scanner(State state, int i, String word);

    void completer(State state, int i);

    Chart[] parse(String[] words);

    Chart[] parseOnTime(String[] words);
}
