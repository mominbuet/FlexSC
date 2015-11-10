/*
 * Md. Momin Al Aziz(momin.aziz.cse gmail)
 * http://www.mominalaziz.com
 * 
 */
package circuits.arithmetic;

import circuits.CircuitLib;
import flexsc.CompEnv;
import util.Utils;

/**
 *
 * @author momin
 */
public class StringLib<T> extends CircuitLib<T> {

    public StringLib(CompEnv<T> e) {
        super(e);
    }

    public T[] add(T[] x, T[] y) {
        return y;
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
