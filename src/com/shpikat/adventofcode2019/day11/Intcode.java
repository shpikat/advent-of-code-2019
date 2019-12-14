package com.shpikat.adventofcode2019.day11;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.function.LongBinaryOperator;

class Intcode implements Callable<Void> {

    private final long[] program;
    private final BlockingQueue<Long> input;
    private final BlockingQueue<Long> output;

    Intcode(final long[] program, final BlockingQueue<Long> input, final BlockingQueue<Long> output) {
        this.program = program;
        this.input = input;
        this.output = output;
    }

    public Void call() throws InterruptedException {
        final Runtime runtime = new Runtime(program, input, output);
        runtime.execute();
        return null;
    }

    private static class Runtime {
        private static final int MAX_ARGUMENTS = 3;

        private final BlockingQueue<Long> input;
        private final BlockingQueue<Long> output;
        private long[] memory;
        private int ip = 0;
        private int relativeBase = 0;

        Runtime(final long[] program, final BlockingQueue<Long> input, final BlockingQueue<Long> output) {
            memory = program.clone();
            this.input = input;
            this.output = output;
        }

        void execute() throws InterruptedException {
            while (readMemory(ip) != 99) {
                final int opcode = (int) readMemory(ip) % 100;
                final int mode = (int) readMemory(ip) / 100;

                final int[] modes = getArgumentsModes(mode);
                switch (opcode) {
                    case 1:
                        writeBinaryOperationResult(Math::addExact, modes);
                        ip += 4;
                        break;

                    case 2:
                        writeBinaryOperationResult(Math::multiplyExact, modes);
                        ip += 4;
                        break;

                    case 3:
                        writeMemory(input.take(), translateAddress(1, modes[1]));
                        ip += 2;
                        break;

                    case 4:
                        output.put(getFirstParameter(modes));
                        ip += 2;
                        break;

                    case 5:
                        ip = getFirstParameter(modes) != 0 ? (int) getSecondParameter(modes) : ip + 3;
                        break;

                    case 6:
                        ip = getFirstParameter(modes) == 0 ? (int) getSecondParameter(modes) : ip + 3;
                        break;

                    case 7:
                        writeBinaryOperationResult((a, b) -> a < b ? 1 : 0, modes);
                        ip += 4;
                        break;

                    case 8:
                        writeBinaryOperationResult((a, b) -> a == b ? 1 : 0, modes);
                        ip += 4;
                        break;

                    case 9:
                        relativeBase += getFirstParameter(modes);
                        ip += 2;
                        break;

                    default:
                        throw new IllegalArgumentException("Unexpected opcode: " + opcode);
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
            final int physicalAddress;
            switch (mode) {
                case 0:
                    physicalAddress = (int) readMemory(givenAddress);
                    break;
                case 1:
                    physicalAddress = givenAddress;
                    break;
                case 2:
                    physicalAddress = relativeBase + (int) readMemory(givenAddress);
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected mode " + mode);
            }
            return physicalAddress;
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
                System.out.println(String.format("%d -> %d", memory.length, request));
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
