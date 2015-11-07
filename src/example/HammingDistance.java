package example;

import util.EvaRunnable;
import util.GenRunnable;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import gc.BadLabelException;
import java.util.Arrays;

public class HammingDistance {

    static public <T> T[] compute(CompEnv<T> gen, T[] inputA, T[] inputB) {
        return new IntegerLib<T>(gen).hammingDistance(inputA, inputB);
    }

    private static boolean[] stringToBinary(String hexBits) {
//            String s = "foo";
        byte[] bytes = hexBits.getBytes();
        StringBuilder binary = new StringBuilder();
        boolean[] ret = new boolean[bytes.length * 8];
        int j=0;
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
                ret[j++] = !((val & 128) == 0);
            }
            //   binary.append(' ');
        }
        System.out.println("'" + hexBits + "' to binary: " + binary);
        return ret;
    }

    public static class Generator<T> extends GenRunnable<T> {

        T[] inputA;
        T[] inputB;
        T[] scResult;

        @Override
        public void prepareInput(CompEnv<T> gen) {
//            byte[] bytes = stringToBinary(args[0]);
            boolean[] in = stringToBinary(args[0]);
//            for (int i = 0; i < bytes.length; ++i) {
//                in[i] = bytes[i] == 1;
//            }
            inputA = gen.inputOfAlice(in);
            gen.flush();
            inputB = gen.inputOfBob(new boolean[in.length]);
        }

        @Override
        public void secureCompute(CompEnv<T> gen) {
            scResult = compute(gen, inputA, inputB);
        }

        @Override
        public void prepareOutput(CompEnv<T> gen) throws BadLabelException {
            System.out.println(Arrays.toString(gen.outputToAlice(scResult)));
        }

    }

    public static class Evaluator<T> extends EvaRunnable<T> {

        T[] inputA;
        T[] inputB;
        T[] scResult;

        @Override
        public void prepareInput(CompEnv<T> gen) {
//            byte[] bytes = stringToBinary(args[0]);
//            boolean[] in = new boolean[bytes.length];
//            for (int i = 0; i < bytes.length; ++i) {
//                in[i] = bytes[i] == 1;
//            }
            boolean[] in = stringToBinary(args[0]);
            inputA = gen.inputOfAlice(new boolean[in.length]);
            gen.flush();
            inputB = gen.inputOfBob(in);
        }

        @Override
        public void secureCompute(CompEnv<T> gen) {
            scResult = compute(gen, inputA, inputB);
        }

        @Override
        public void prepareOutput(CompEnv<T> gen) throws BadLabelException {
            gen.outputToAlice(scResult);
        }
    }
}
