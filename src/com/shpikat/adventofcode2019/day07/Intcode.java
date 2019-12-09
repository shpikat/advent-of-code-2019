package com.shpikat.adventofcode2019.day07;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

class Intcode implements Callable<Void> {

    @SuppressWarnings("DuplicatedCode")
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
