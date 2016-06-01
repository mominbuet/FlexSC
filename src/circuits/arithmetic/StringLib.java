/*
 * Md. Momin Al Aziz(momin.aziz.cse gmail)
 * http://www.mominalaziz.com
 * 
 */
package circuits.arithmetic;

import circuits.CircuitLib;
import flexsc.CompEnv;
import gc.GCSignal;
import util.Utils;
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

    private int toDecimal(T[] arr) {
        boolean[] tmp = new boolean[arr.length];
        for (int i = 0; i < arr.length; i++) {
            tmp[i] = ((GCSignal) arr[i]).v;
        }
        return Utils.toInt(tmp);
    }

    private T[] tobinary(int val, int length) {
        T[] res = zeros(length);

        if (val < 0) {
            val = 0;
        }
//        char[] bin = Integer.toBinaryString(val).toCharArray();
//        for (int j = 0 + (16 - bin.length); j < 16; j++) {
//            if (bin[j - (16 - bin.length)] == '1') {
//                res[j] = SIGNAL_ONE;//res.length - bin.length + 
//            }
//        }
        boolean[] arr = Utils.fromInt(val, length);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]) {
                res[i] = SIGNAL_ONE;
            }
        }
        return res;
    }

    public T[] editDistance(T[] x, T[] y) {
//        System.out.println("size x " + x.length + " y " + y.length);
        T[] costs = zeros(y.length * 2 + 16);
        for (int j = 0; j < y.length / 8 + 1; j++) {
            System.arraycopy(tobinary(j, 16), 0, costs, j * 16, 16);
//            char[] bin = Integer.toBinaryString(j).toCharArray();
//            for (int k = (16 - bin.length) + j * 16; k < 16 + j * 16; k++) {
//                if (bin[k - (16 - bin.length) - j * 16] == '1') {
//                    costs[k] = SIGNAL_ONE;
//                }
//            }
        }

        for (int i = 8; i <= x.length; i += 8) {
            T[] nw = tobinary(i - 8 - 1, 16);
            System.out.println("nw " + toDecimal(nw));
//            char[] bin = Integer.toBinaryString(i).toCharArray();
//            for (int j = 0 + (16 - bin.length); j < 16; j++) {
//                if (bin[j - (16 - bin.length)] == '1') {
//                    costs[j] = SIGNAL_ONE;//costs.length - bin.length +
//                }
//            }
            System.arraycopy(tobinary(i - 8, 16), 0, costs, 0, 16);
            T[] xChar = Arrays.copyOfRange(x, i - 8, i);
            int costCounter = 16;
            for (int j = 8; j <= y.length; j += 8) {
                T[] yChar = Arrays.copyOfRange(y, j - 8, j);
                T[] costJ = Arrays.copyOfRange(costs, costCounter, costCounter + 16);
                T[] costJPrev = Arrays.copyOfRange(costs, costCounter - 16, costCounter);

                System.out.println("costJ " + toDecimal(costJ) + " costJPrev " + toDecimal(costJPrev));
                T[] minCostJJPrev = min(costJ, costJPrev);
                minCostJJPrev = incrementByOne(minCostJJPrev);
//                System.out.println(Arrays.toString(yChar));
                T t = eq(xChar, yChar);
//                System.out.println("t class  " + ((GCSignal)t).getLSB());
//                if (!((GCSignal) t).v) {
                if (t == SIGNAL_ZERO) {
                    nw = incrementByOne(nw);
                    System.out.println("MisMatch");
                } else {
                    System.out.println("Match" + j);
                }
                T[] cj = min(nw, minCostJJPrev);
                nw = costJ;
                costJ = cj;

                System.out.println("cost " + toDecimal(cj) + " nw " + toDecimal(nw));
//                T[] tmp = env.newTArray(costs.length);
//                tmp[tmp.length] = t;
//                costs = add(costs, tmp);
                System.arraycopy(costJ, 0, costs, costCounter, 16);
                costCounter += 16;
            }

        }
        T[] res = zeros(16);
        boolean[] intres = Utils.fromInt(toDecimal(Arrays.copyOfRange(costs, costs.length - 16, costs.length)), 16);
        for (int i = 0; i < res.length; i++) {
            if (intres[i]) {
                res[i] = SIGNAL_ONE;
            }
        }
        return res;
//        return costs[costs.length];
    }

    public T[] incrementByOne(T[] x) {
        T[] one = zeros(x.length);
        one[0] = SIGNAL_ONE;
        return add(x, one);
    }

    public T[] hammingDistance(T[] x, T[] y) {
        T[] a = xor(x, y);
        return numberOfOnes(a);
    }

    public T[] numberOfOnes(T[] t) {
        if (t.length == 0) {
            T[] res = env.newTArray(1);
            res[0] = SIGNAL_ZERO;
            return res;
        }
        if (t.length == 1) {
            return t;
        } else {
            int length = 1;
            int w = 1;
            while (length <= t.length) {
                length <<= 1;
                w++;
            }
            length >>= 1;

            T[] res1 = numberOfOnesN(Arrays.copyOfRange(t, 0, length));
            T[] res2 = numberOfOnes(Arrays.copyOfRange(t, length, t.length));
            return add(padSignal(res1, w), padSignal(res2, w));
        }
    }

    public T[] numberOfOnesN(T[] res) {
        if (res.length == 1) {
            return res;
        }
        T[] left = numberOfOnesN(Arrays.copyOfRange(res, 0, res.length / 2));
        T[] right = numberOfOnesN(Arrays.copyOfRange(res, res.length / 2, res.length));
        return unSignedAdd(left, right);
    }

    public T[] unSignedAdd(T[] x, T[] y) {
        assert (x != null && y != null && x.length == y.length) : "add: bad inputs.";
        T[] res = env.newTArray(x.length + 1);

        T[] t = add(x[0], y[0], env.newT(false));
        res[0] = t[S];
        for (int i = 0; i < x.length - 1; i++) {
            t = add(x[i + 1], y[i + 1], t[COUT]);
            res[i + 1] = t[S];
        }
        res[res.length - 1] = t[COUT];
        return res;
    }

    public T eq(T x, T y) {
        assert (x != null && y != null) : "StringLib.eq: bad inputs";

        return not(xor(x, y));
    }

    public T eq(T[] x, T[] y) {
        assert (x != null && y != null && x.length == y.length) : "StringLib.eq[]: bad inputs.";

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
