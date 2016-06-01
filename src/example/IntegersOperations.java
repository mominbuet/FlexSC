/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import util.EvaRunnable;
import util.GenRunnable;
import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import gc.BadLabelException;
import gc.GCSignal;
import java.util.Arrays;

public class IntegersOperations {

    static public <T> T[] compute(CompEnv<T> gen, T[] inputA, T[] inputB) {
//        return new IntegerLib<T>(gen).add(inputA, inputB);
        return new IntegerLib<T>(gen).add(inputA, inputB);

    }

    public static class Generator<T> extends GenRunnable<T> {

        T[] inputA;
        T[] inputB;
        T[] scResult;

        @Override
        public void prepareInput(CompEnv<T> gen) {
            inputA = gen.inputOfAlice(Utils.fromInt(new Integer(args[0]), 32));
            //System.out.println("Input from Alice" + ((GCSignal) inputA[0]).toString());
            gen.flush();
            inputB = gen.inputOfBob(new boolean[32]);
        }

        @Override
        public void secureCompute(CompEnv<T> gen) {
            scResult = compute(gen, inputA, inputB);
        }

        @Override
        public void prepareOutput(CompEnv<T> gen) throws BadLabelException {
//            System.out.println(Arrays.toString(gen.outputToAlice(scResult)));
            System.out.println("Output " + Utils.toInt(gen.outputToAlice(scResult)));
        }
    }

    public static class Evaluator<T> extends EvaRunnable<T> {

        T[] inputA;
        T[] inputB;
        T[] scResult;

        @Override
        public void prepareInput(CompEnv<T> gen) {
            inputA = gen.inputOfAlice(new boolean[32]);
            gen.flush();
            inputB = gen.inputOfBob(Utils.fromInt(new Integer(args[0]), 32));
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
