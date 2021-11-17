package com.shpikat.adventofcode2019;

import java.util.Arrays;

public class Day05 {
    static class Part2 {
        static int solve(final String input, final int inputValue) {
            final int[] program = Arrays
                    .stream(input.split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            return new Intcode(program, inputValue).run();
        }

        private static class Intcode {
            private final int[] program;
            private final int input;

            public Intcode(final int[] program, final int input) {
                this.program = program;
                this.input = input;
            }

            public int run() {
                int ip = 0;
                int output = 0;
                while (program[ip] != 99) {
                    final int opcode = program[ip] % 100;
                    final int mode = program[ip] / 100;
                    switch (opcode) {
                        case 1 -> {
                            final int[] modes = getModes(mode, 3);
                            final int a = getParameter(program, ip + 1, modes[0]);
                            final int b = getParameter(program, ip + 2, modes[1]);
                            program[program[ip + 3]] = a + b;
                            ip += 4;
                        }
                        case 2 -> {
                            final int[] modes = getModes(mode, 3);
                            final int a = getParameter(program, ip + 1, modes[0]);
                            final int b = getParameter(program, ip + 2, modes[1]);
                            program[program[ip + 3]] = a * b;
                            ip += 4;
                        }
                        case 3 -> {
                            program[program[ip + 1]] = input;
                            ip += 2;
                        }
                        case 4 -> {
                            output = getParameter(program, ip + 1, getModes(mode, 1)[0]);
                            ip += 2;
                        }
                        case 5 -> {
                            final int[] modes = getModes(mode, 2);
                            ip = getParameter(program, ip + 1, modes[0]) != 0
                                    ? getParameter(program, ip + 2, modes[1])
                                    : ip + 3;
                        }
                        case 6 -> {
                            final int[] modes = getModes(mode, 2);
                            ip = getParameter(program, ip + 1, modes[0]) == 0
                                    ? getParameter(program, ip + 2, modes[1])
                                    : ip + 3;
                        }
                        case 7 -> {
                            final int[] modes = getModes(mode, 3);
                            final int a = getParameter(program, ip + 1, modes[0]);
                            final int b = getParameter(program, ip + 2, modes[1]);
                            program[program[ip + 3]] = a < b ? 1 : 0;
                            ip += 4;
                        }
                        case 8 -> {
                            final int[] modes = getModes(mode, 3);
                            final int a = getParameter(program, ip + 1, modes[0]);
                            final int b = getParameter(program, ip + 2, modes[1]);
                            program[program[ip + 3]] = a == b ? 1 : 0;
                            ip += 4;
                        }
                    }
                }
                return output;
            }

            private static int[] getModes(int mode, final int count) {
                final int[] modes = new int[count];
                int i = 0;
                while (mode != 0) {
                    modes[i++] = mode % 10;
                    mode /= 10;
                }
                return modes;
            }

            private static int getParameter(final int[] program, final int pointer, final int mode) {
                return mode == 0
                        ? program[program[pointer]]
                        : program[pointer];
            }
        }
    }
}
