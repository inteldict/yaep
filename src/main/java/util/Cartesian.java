package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Denis Krusko
 * @author e-mail: kruskod@gmail.com
 */
public class Cartesian {

    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

//    public static void generate(int[][] sets) {
//        int solutions = 1;
//        for(int i = 0; i < sets.length; solutions *= sets[i].length, i++);
//        for(int i = 0; i < solutions; i++) {
//            int j = 1;
//            for(int[] set : sets) {
//                System.out.print(set[(i/j)%set.length] + " ");
//                j *= set.length;
//            }
//            System.out.println();
//        }
//    }


    public static <T> List<List<T>> cartesianProduct(List<List<T>> sets) {
        int solutions = 1;
        int i, j, k;
        for (i = 0; i < sets.size(); solutions *= sets.get(i).size(), i++) ;

        List<List<T>> combinations = new ArrayList<>();
        List<T> combination;
        List<T> set;
        for (i = 0; i < solutions; i++) {
            j = 1;
            combination = new ArrayList<>();
            for (k = 0; k < sets.size(); k++) {
                set = sets.get(k);
                combination.add(set.get((i / j) % set.size()));
                j *= set.size();
            }
            combinations.add(combination);
        }

        return combinations;
    }

    public static void main(String[] args) {
        List<Integer> a = new ArrayList<>();
        a.add(1);
        a.add(2);
        a.add(3);

        List<Integer> b = new ArrayList<>();
        b.add(4);
        b.add(5);
        b.add(6);

        List<Integer> c = new ArrayList<>();

        c.add(7);
        c.add(8);
        c.add(9);

        List<List<Integer>> sets = new ArrayList<>();
        sets.add(a);
        sets.add(b);
        sets.add(c);

        log.info(cartesianProduct(sets).toString());
    }

}
