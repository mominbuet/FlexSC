/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import circuits.arithmetic.IntegerLib;
import circuits.arithmetic.StringLib;
import flexsc.CompEnv;
import gc.BadLabelException;
import util.EvaRunnable;
import util.GenRunnable;
import util.Utils;

/**
 *
 * @author azizmma
 */
public class HashEquality {

    static public <T> T[] compute(CompEnv<T> gen, T[][] inputA, T[][] inputB) {
        return new StringLib<T>(gen).equalArray(inputA, inputB);
    }

    public static class Generator<T> extends GenRunnable<T> {

        T[][] inputA;
        T[][] inputB;
        T[] scResult;

        @Override
        public void prepareInput(CompEnv<T> gen) {
            inputA = gen.newTArray(10, 8);
            boolean[][] in = new boolean[10][8];
            for (int i = 0; i < 10; i++) {
                in[i] = Utils.fromString(i + "");
//                System.out.println("input size " + in[i].length);
                inputA[i] = gen.inputOfAlice(Utils.fromString(i + ""));
            }

            gen.flush();
            inputB = gen.newTArray(10, 8);
            for (int i = 0; i < 10; i++) {
                inputB[i] = gen.inputOfBob(new boolean[in[i].length]);
            }

        }

        @Override
        public void secureCompute(CompEnv<T> gen) {
            scResult = compute(gen, inputA, inputB);
        }

        @Override
        public void prepareOutput(CompEnv<T> gen) throws BadLabelException {
            for (int i = 0; i < 10; i++) {
                System.out.println((gen.outputToAlice(scResult[i])));
            }

        }
    }

    public static class Evaluator<T> extends EvaRunnable<T> {

        T[][] inputA;
        T[][] inputB;
        T[] scResult;

        @Override
        public void prepareInput(CompEnv<T> gen) {
//            boolean[] in = Utils.fromInt(Integer.parseInt(args[0]), 16);
//
//            inputA = gen.inputOfAlice(new boolean[in.length]);
//            gen.flush();
//            inputB = gen.inputOfBob(in);
            inputA = gen.newTArray(10, 8);
            boolean[][] in = new boolean[10][8];

            for (int i = 0; i < 10; i++) {
                inputA[i] = gen.inputOfAlice(new boolean[in[i].length]);
            }
            gen.flush();
            inputB = gen.newTArray(10, 8);
            for (int i = 0; i < 10; i++) {
                in[i] = Utils.fromString("2");
                inputB[i] = gen.inputOfBob(in[i]);
            }
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
