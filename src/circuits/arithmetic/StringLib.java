/*
 * Md. Momin Al Aziz(momin.aziz.cse gmail)
 * http://www.mominalaziz.com
 * 
 */
package circuits.arithmetic;

import circuits.CircuitLib;
import flexsc.CompEnv;
import java.lang.reflect.Array;
import util.Utils;
import java.lang.UnsupportedOperationException;
import java.util.Arrays;

/**
 *
 * @author momin
 */
public class StringLib<T> extends CircuitLib<T> {

    public StringLib(CompEnv<T> e) {
        super(e);
    }
    static final int S = 0;
    static final int COUT = 1;

    public T Contains(T[] x, T[] y) {
        throw new UnsupportedOperationException("Getting there soon");
    }

    // full 1-bit adder
    public T[] add(T x, T y, T cin) {
        T[] res = env.newTArray(2);

        T t1 = xor(x, cin);
        T t2 = xor(y, cin);
        res[S] = xor(x, t2);
        t1 = and(t1, t2);
        res[COUT] = xor(cin, t1);

        return res;
    }

    // full n-bit adder
    public T[] addFull(T[] x, T[] y, boolean cin) {
        assert (x != null && y != null && x.length == y.length) : "add: bad inputs.";

        T[] res = env.newTArray(x.length + 1);
        T[] t = add(x[0], y[0], env.newT(cin));
        res[0] = t[S];
        for (int i = 0; i < x.length - 1; i++) {
            t = add(x[i + 1], y[i + 1], t[COUT]);
            res[i + 1] = t[S];
        }
        res[res.length - 1] = t[COUT];
        return res;
    }

    public T[] min(T[] x, T[] y) {
        T leq = leq(x, y);
        return mux(y, x, leq);
    }

    public T geq(T[] x, T[] y) {
        assert (x.length == y.length) : "bad input";

        T[] result = sub(x, y);
        return not(result[result.length - 1]);
    }

    public T leq(T[] x, T[] y) {
        return geq(y, x);
    }

    public T[] add(T[] x, T[] y, boolean cin) {
        return Arrays.copyOf(addFull(x, y, cin), x.length);
    }

    public T[] sub(T[] x, T[] y) {
        assert (x != null && y != null && x.length == y.length) : "sub: bad inputs.";

        return add(x, not(y), true);
    }

    public T[] add(T[] x, T[] y) {

        return add(x, y, false);
    }

    private T[] tobinary(int val, int length) {
        T[] res = zeros(length);
        if (val < 0) {
            val = 0;
        }
        char[] bin = Integer.toBinaryString(val).toCharArray();
        for (int j = 0 + (16 - bin.length); j < 16; j++) {
            if (bin[j - (16 - bin.length)] == '1') {
                res[j] = SIGNAL_ONE;//res.length - bin.length + 
            }
        }
        return res;
    }

    public T[] editDistance(T[] x, T[] y) {
//        System.out.println("size x " + x.length + " y " + y.length);
        T[] costs = zeros(y.length * 2 + 16);
        for (int j = 1; j < y.length / 8 + 1; j++) {
            char[] bin = Integer.toBinaryString(j).toCharArray();
            for (int k = (16 - bin.length) + j * 16; k < 16 + j * 16; k++) {
                if (bin[k - (16 - bin.length) - j * 16] == '1') {
                    costs[k] = SIGNAL_ONE;
                }
            }
        }

        for (int i = 8; i <= x.length; i += 8) {
            T[] nw = tobinary(i - 1, 16);
            char[] bin = Integer.toBinaryString(i).toCharArray();
            for (int j = 0 + (16 - bin.length); j < 16; j++) {
                if (bin[j - (16 - bin.length)] == '1') {
                    costs[j] = SIGNAL_ONE;//costs.length - bin.length +
                }
            }
            T[] xChar = Arrays.copyOfRange(x, i - 8, i);

            for (int j = 8; j <= y.length; j += 8) {
                T[] yChar = Arrays.copyOfRange(y, j - 8, j);
                T[] costJ = Arrays.copyOfRange(costs, j - 8 + 16, j - 8 + 16 + 16);
                T[] costJPrev = Arrays.copyOfRange(costs, j - 8, j - 8 + 16);
                T[] minCostJJPrev = min(costJ, costJPrev);
                minCostJJPrev = incrementByOne(minCostJJPrev);
//                System.out.println(Arrays.toString(yChar));
                T t = eq(xChar, yChar);
                if (t == SIGNAL_ZERO) {
                    nw = incrementByOne(nw);
                    System.out.println("MisMatch");
                } else {
                    System.out.println("Match" + j);
                }
                T[] cj = min(nw, minCostJJPrev);
                nw = costJ;
                costJ = cj;
//                T[] tmp = env.newTArray(costs.length);
//                tmp[tmp.length] = t;
//                costs = add(costs, tmp);
                System.arraycopy(costJ, 0, costs, j - 8 + 16, 16);
            }

        }

        return Arrays.copyOfRange(costs, costs.length - 16, costs.length);
//        return costs[costs.length];
    }

    public T[] incrementByOne(T[] x) {
        T[] one = zeros(x.length);
        one[0] = SIGNAL_ONE;
        return add(x, one);
    }

    public T[] hammingDistance(T[] x, T[] y) {
        throw new UnsupportedOperationException("Getting there soon");
    }

    public T eq(T x, T y) {
        assert (x != null && y != null) : "CircuitLib.eq: bad inputs";

        return not(xor(x, y));
    }

    public T eq(T[] x, T[] y) {
        assert (x != null && y != null && x.length == y.length) : "CircuitLib.eq[]: bad inputs.";

        T res = env.newT(true);
        for (int i = 0; i < x.length; i++) {
            T t = eq(x[i], y[i]);
            res = env.and(res, t);
        }

        return res;
    }

    /**
     * Concatenates two strings
     *
     * @param x
     * @param y
     * @return
     */
    public T[] append(T[] x, T[] y) {
        //@SuppressWarnings("unchecked")

        T[] result = env.newTArray(x.length + y.length);
        System.arraycopy(x, 0, result, 0, x.length);
        System.arraycopy(y, 0, result, x.length, y.length);
        return result;
    }

    public String outputToAlice(T[] a) {
        return Utils.toString(env.outputToAlice(a));
    }

    public T[] inputOfAlice(String d) {
        return env.inputOfAlice(Utils.fromString(d));
    }

    public T[] inputOfBob(String d) {
        return env.inputOfBob(Utils.fromString(d));
    }
}
