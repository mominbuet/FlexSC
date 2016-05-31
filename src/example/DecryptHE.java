package example;

import circuits.arithmetic.HELib;
import circuits.arithmetic.IntegerLib;
import static example.HammingDistanceString.compute;
import flexsc.CompEnv;
import gc.BadLabelException;
import java.math.BigInteger;
import java.util.Arrays;
import util.EvaRunnable;
import util.GenRunnable;
import util.Paillier;
import util.Utils;

/**
 *
 * @author momin.aziz.cse@gmail.com
 */
public class DecryptHE {

    static public <T> T[] compute(CompEnv<T> gen, T[] inputA, T[] inputB, T[] inputNSquare) {

        return new HELib<T>(gen).decrypt(inputA, inputB, inputNSquare);
    }

    public static class Generator<T> extends GenRunnable<T> {

        T[] inputA;
        T[] inputB;
        T[] inputNSquare;
        T[] scResult;

        @Override
        public void prepareInput(CompEnv<T> gen) {
            System.out.println("Input Decrypted " + new Paillier(true).Decryption(new BigInteger(args[0])));
            System.out.println("Input " + new BigInteger(args[0]));
            inputA = gen.inputOfAlice(Utils.fromBigInteger(new BigInteger(args[0]), 1024));
            inputNSquare = gen.inputOfAlice(Utils.fromBigInteger(new BigInteger(args[0]), 1024));
            inputB = gen.inputOfBob(new boolean[1024]);
        }

        @Override
        public void secureCompute(CompEnv<T> gen) {
            scResult = compute(gen, inputA, inputB, inputNSquare);
        }

        @Override
        public void prepareOutput(CompEnv<T> gen) throws BadLabelException {
//            System.out.println("GEN1 " + Utils.toBigInteger(gen.outputToAlice(scResult)));
            System.out.println("Output Gen " + Utils.toBigInteger(gen.outputToAlice(scResult)));
//            System.out.println("Output Gen " + new Paillier(true).Decryption(Utils.toBigInteger(gen.outputToAlice(scResult))));
        }

    }

    public static class Evaluator<T> extends EvaRunnable<T> {

        T[] inputA;
        T[] inputB;
        T[] inputNSquare;
        T[] scResult;

        @Override
        public void prepareInput(CompEnv<T> gen) {

            inputA = gen.inputOfAlice(new boolean[1024]);
            inputNSquare = gen.inputOfAlice(new boolean[1024]);
            gen.flush();
            boolean[] in = Utils.fromBigInteger(new BigInteger(args[0]), 1024);
            inputB = gen.inputOfBob(in);
            System.out.println("Input from Evaluator:" + Arrays.toString(in));
        }

        @Override
        public void secureCompute(CompEnv<T> gen) {
            scResult = compute(gen, inputA, inputB, inputNSquare);
        }

        @Override
        public void prepareOutput(CompEnv<T> gen) throws BadLabelException {
            gen.outputToAlice(scResult);
        }
    }

}
