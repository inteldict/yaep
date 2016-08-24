import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class Node {
    protected final CharSequence value;
    protected Integer from;
    protected Integer to;
    protected final List<Node> children = new ArrayList<>();

    public Node(CharSequence value) {
        this.value = value;
    }

    public Node(CharSequence value, int from, int to) {
        this.value = value;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return children.isEmpty()? value.toString() :
                value + " ->" + children.stream().map(n -> n.value).collect(Collectors.joining(" "));
    }
}
