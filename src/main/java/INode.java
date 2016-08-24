/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public interface INode {

    void setSymbol(CharSequence symbol);

    void setFrom(Integer from);

    void setTo(Integer to);

    CharSequence getSymbol();

    Integer getFrom();

    Integer getTo();

    String toString();

    CharSequence prettyPrint(int level);
}
