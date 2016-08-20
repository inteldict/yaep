import org.junit.Assert;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class TerminalRule extends Rule {

    protected final String[] rhs;

    public TerminalRule(NT lhs, String rhs) {
        super(lhs);
        this.rhs = rhs;
    }

    @Override
    public int getRHSLength() {
        return 1;
    }

    @Override
    public boolean isTerminal(int index) {
        return true;
    }

    public String getSymbol(int index) {
        assert index == 0;
        return rhs;
    }

    @Override
    public String toStringWithDot(int dot) {
        if (dot == 0) {
            return "* " + rhs;
        } else {
            return rhs + " *";
        }
    }
}
