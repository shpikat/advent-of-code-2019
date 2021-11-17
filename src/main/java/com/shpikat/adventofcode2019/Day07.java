package com.shpikat.adventofcode2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Day07 {
    static class Part2 {
        static int solve(final String input) throws InterruptedException, ExecutionException {
            final int[] program = Arrays
                    .stream(input.split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            final List<Integer[]> allSequences = createPermutations();
            final ExecutorService executorService = Executors.newFixedThreadPool(5);
            final BlockingQueue<Integer> outputA = new ArrayBlockingQueue<>(10);
            final BlockingQueue<Integer> outputB = new ArrayBlockingQueue<>(10);
            final BlockingQueue<Integer> outputC = new ArrayBlockingQueue<>(10);
            final BlockingQueue<Integer> outputD = new ArrayBlockingQueue<>(10);
            final BlockingQueue<Integer> outputE = new ArrayBlockingQueue<>(10);

            // Meaningful names for the I/O bindings
            @SuppressWarnings("UnnecessaryLocalVariable") final BlockingQueue<Integer> inputA = outputE;
            @SuppressWarnings("UnnecessaryLocalVariable") final BlockingQueue<Integer> inputB = outputA;
            @SuppressWarnings("UnnecessaryLocalVariable") final BlockingQueue<Integer> inputC = outputB;
            @SuppressWarnings("UnnecessaryLocalVariable") final BlockingQueue<Integer> inputD = outputC;
            @SuppressWarnings("UnnecessaryLocalVariable") final BlockingQueue<Integer> inputE = outputD;

            final Collection<Callable<Void>> amplifiers = Arrays.asList(
                    new Intcode(program, inputA, outputA),
                    new Intcode(program, inputB, outputB),
                    new Intcode(program, inputC, outputC),
                    new Intcode(program, inputD, outputD),
                    new Intcode(program, inputE, outputE));
            int max = 0;
            for (final Integer[] sequence : allSequences) {
                // settings
                inputA.put(sequence[0]);
                inputB.put(sequence[1]);
                inputC.put(sequence[2]);
                inputD.put(sequence[3]);
                inputE.put(sequence[4]);

                // input
                inputA.put(0);

                final List<Future<Void>> futures = executorService.invokeAll(amplifiers);

                // verify the execution completed without any exceptions
                for (final Future<Void> future : futures) {
                    future.get();
                }

                final int result = outputE.remove();
                max = Math.max(max, result);
            }

            executorService.shutdown();
            return max;
        }

        // Heap's algorithm non-recursive implementation
        private static List<Integer[]> createPermutations() {
            final Integer[] elements = new Integer[]{5, 6, 7, 8, 9};
            final int nElements = elements.length;
            final List<Integer[]> permutations = new ArrayList<>(120); // 5!

            final int[] indexes = new int[nElements];
            int i = 0;
            permutations.add(elements.clone());
            while (i < nElements) {
                if (indexes[i] < i) {
                    swap(elements, (i & 1) == 0 ? 0 : indexes[i], i);
                    permutations.add(elements.clone());
                    indexes[i]++;
                    i = 0;
                } else {
                    indexes[i++] = 0;
                }
            }
            return permutations;
        }

        private static <T> void swap(final T[] arr, final int i, final int j) {
            T tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }

        private static class Intcode implements Callable<Void> {

            private final Operation[] operations = new Operation[]{
                    (program, ip, mode) -> -1,
                    new Operation() {
                        @Override
                        public int execute(final int[] program, final int ip, final int[] modes) {
                            final int a = getParameter(program, ip + 1, modes[0]);
                            final int b = getParameter(program, ip + 2, modes[1]);
                            final int c = a + b;
                            program[program[ip + 3]] = c;
                            return ip + 4;
                        }
                    },
                    new Operation() {
                        @Override
                        public int execute(final int[] program, final int ip, final int[] modes) {
                            final int a = getParameter(program, ip + 1, modes[0]);
                            final int b = getParameter(program, ip + 2, modes[1]);
                            final int c = a * b;
                            program[program[ip + 3]] = c;
                            return ip + 4;
                        }
                    },
                    new Operation() {
                        @Override
                        public int execute(final int[] program, final int ip, final int[] modes) throws InterruptedException {
                            program[program[ip + 1]] = input.take();
                            return ip + 2;
                        }
                    },
                    new Operation() {
                        @Override
                        public int execute(final int[] program, final int ip, final int[] modes) throws InterruptedException {
                            final int val = getParameter(program, ip + 1, modes[0]);
                            final int nextIp = ip + 2;
                            output.put(val);
                            return nextIp;
                        }
                    },
                    new Operation() {
                        @Override
                        public int execute(final int[] program, final int ip, final int[] modes) {
                            return getParameter(program, ip + 1, modes[0]) != 0
                                    ? getParameter(program, ip + 2, modes[1])
                                    : ip + 3;
                        }
                    },
                    new Operation() {
                        @Override
                        public int execute(final int[] program, final int ip, final int[] modes) {
                            return getParameter(program, ip + 1, modes[0]) == 0
                                    ? getParameter(program, ip + 2, modes[1])
                                    : ip + 3;
                        }
                    },
                    new Operation() {
                        @Override
                        public int execute(final int[] program, final int ip, final int[] modes) {
                            final int a = getParameter(program, ip + 1, modes[0]);
                            final int b = getParameter(program, ip + 2, modes[1]);
                            program[program[ip + 3]] = a < b ? 1 : 0;
                            return ip + 4;
                        }
                    },
                    new Operation() {
                        @Override
                        public int execute(final int[] program, final int ip, final int[] modes) {
                            final int a = getParameter(program, ip + 1, modes[0]);
                            final int b = getParameter(program, ip + 2, modes[1]);
                            program[program[ip + 3]] = a == b ? 1 : 0;
                            return ip + 4;
                        }
                    }
            };

            private final int[] program;
            private final BlockingQueue<Integer> input;
            private final BlockingQueue<Integer> output;

            Intcode(final int[] program, final BlockingQueue<Integer> input, final BlockingQueue<Integer> output) {
                this.program = program;
                this.input = input;
                this.output = output;
            }

            public Void call() throws InterruptedException {
                int ip = 0;
                final int[] memory = program.clone();
                while (memory[ip] != 99) {
                    final int opcode = memory[ip] % 100;
                    final int mode = memory[ip] / 100;
                    final Operation operation = getOperation(opcode);
                    ip = operation.execute(memory, ip, getModes(mode));
                }
                return null;
            }

            private Operation getOperation(final int opCode) {
                return operations[opCode];
            }

            private static int[] getModes(int mode) {
                final int[] modes = new int[4];
                int i = 0;
                while (mode != 0) {
                    modes[i++] = mode % 10;
                    mode /= 10;
                }
                return modes;
            }

            private interface Operation {
                int execute(final int[] program, final int ip, final int[] modes) throws InterruptedException;

                default int getParameter(final int[] program, final int pointer, final int mode) {
                    return mode == 0
                            ? program[program[pointer]]
                            : program[pointer];
                }
            }
        }
    }
}
