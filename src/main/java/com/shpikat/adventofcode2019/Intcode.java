package com.shpikat.adventofcode2019;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;

/**
 * An Intcode computer introduced fully in Day09, enhanced over time, being fully compatible though.
 */
class Intcode implements Callable<Void> {

    private final long[] program;
    protected final BlockingQueue<Long> input;
    protected final BlockingQueue<Long> output;
    private final Consumer<long[]> runtimePatcher;

    private Intcode(final long[] program,
                    final BlockingQueue<Long> input,
                    final BlockingQueue<Long> output,
                    final Consumer<long[]> runtimePatcher) {
        this.program = program;
        this.input = input;
        this.output = output;
        this.runtimePatcher = runtimePatcher;
    }

    public static Intcode fromInput(final String input, final int capacity) {
        return fromInput(input, capacity, memory -> {
        });
    }

    public static Intcode fromInput(final String input, final int capacity, final Consumer<long[]> runtimePatcher) {
        return new Intcode(readProgram(input), createInput(capacity), createOutput(capacity), runtimePatcher);
    }

    @Override
    public Void call() throws InterruptedException {
        final Runtime runtime = new Runtime(program, input, output, runtimePatcher);
        runtime.execute();
        return null;
    }

    protected static long[] readProgram(final String input) {
        return Arrays
                .stream(input.split(","))
                .mapToLong(Long::parseLong)
                .toArray();
    }

    protected static BlockingQueue<Long> createInput(final int capacity) {
        return new ArrayBlockingQueue<>(capacity);
    }

    protected static BlockingQueue<Long> createOutput(final int capacity) {
        return new ArrayBlockingQueue<>(capacity);
    }

    protected static class Runtime {
        private static final int MAX_ARGUMENTS = 3;

        protected final BlockingQueue<Long> input;
        protected final BlockingQueue<Long> output;
        protected long[] memory;
        private int ip = 0;
        private int relativeBase = 0;

        Runtime(final long[] program, final BlockingQueue<Long> input, final BlockingQueue<Long> output, final Consumer<long[]> patcher) {
            memory = program.clone();
            this.input = input;
            this.output = output;
            patcher.accept(memory);
        }

        void execute() throws InterruptedException {
            while (readMemory(ip) != 99) {
                final int opcode = (int) readMemory(ip) % 100;
                final int mode = (int) readMemory(ip) / 100;

                final int[] modes = getArgumentsModes(mode);
                switch (opcode) {
                    case 1 -> {
                        writeBinaryOperationResult(Math::addExact, modes);
                        ip += 4;
                    }
                    case 2 -> {
                        writeBinaryOperationResult(Math::multiplyExact, modes);
                        ip += 4;
                    }
                    case 3 -> {
                        writeMemory(input.take(), translateAddress(1, modes[1]));
                        ip += 2;
                    }
                    case 4 -> {
                        output.put(getFirstParameter(modes));
                        ip += 2;
                    }
                    case 5 -> ip = getFirstParameter(modes) != 0 ? (int) getSecondParameter(modes) : ip + 3;
                    case 6 -> ip = getFirstParameter(modes) == 0 ? (int) getSecondParameter(modes) : ip + 3;
                    case 7 -> {
                        writeBinaryOperationResult((a, b) -> a < b ? 1 : 0, modes);
                        ip += 4;
                    }
                    case 8 -> {
                        writeBinaryOperationResult((a, b) -> a == b ? 1 : 0, modes);
                        ip += 4;
                    }
                    case 9 -> {
                        relativeBase += getFirstParameter(modes);
                        ip += 2;
                    }
                    default -> throw new IllegalArgumentException("Unexpected opcode: " + opcode);
                }
            }
        }

        private void writeBinaryOperationResult(final LongBinaryOperator operation, final int[] modes) {
            final long a = getFirstParameter(modes);
            final long b = getSecondParameter(modes);
            final long result = operation.applyAsLong(a, b);
            final int address = translateAddress(3, modes[3]);
            writeMemory(result, address);
        }

        private long getFirstParameter(final int[] modes) {
            return readMemory(translateAddress(1, modes[1]));
        }

        private long getSecondParameter(final int[] modes) {
            return readMemory(translateAddress(2, modes[2]));
        }

        private int translateAddress(final int offset, final int mode) {
            final int givenAddress = ip + offset;
            return switch (mode) {
                case 0 -> (int) readMemory(givenAddress);
                case 1 -> givenAddress;
                case 2 -> relativeBase + (int) readMemory(givenAddress);
                default -> throw new IllegalArgumentException("Unexpected mode " + mode);
            };
        }

        private long readMemory(final int address) {
            ensureCapacity(address);
            return memory[address];
        }

        private void writeMemory(final long value, final int address) {
            ensureCapacity(address);
            memory[address] = value;
        }

        private void ensureCapacity(final int address) {
            final int requiredLength = address + 1;
            if (memory.length < requiredLength) {
                final int request = Math.max(2 * memory.length, requiredLength);
                memory = Arrays.copyOf(memory, request);
            }
        }

        private static int[] getArgumentsModes(int mode) {
            final int[] modes = new int[MAX_ARGUMENTS + 1];
            int i = 1;
            while (mode != 0) {
                modes[i++] = mode % 10;
                mode /= 10;
            }
            return modes;
        }
    }
}
