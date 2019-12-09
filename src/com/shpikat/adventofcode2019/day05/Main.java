package com.shpikat.adventofcode2019.day05;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private enum OpCode {
        ADD(1) {
            @Override
            int execute(final int[] program, final int ip, final int mode) {
                final int[] modes = getModes(mode, 3);
                final int a = getParameter(program, ip + 1, modes[0]);
                final int b = getParameter(program, ip + 2, modes[1]);
                final int c = a + b;
                program[program[ip + 3]] = c;
                return ip + 4;
            }
        },
        MUL(2) {
            @Override
            int execute(final int[] program, final int ip, final int mode) {
                final int[] modes = getModes(mode, 3);
                final int a = getParameter(program, ip + 1, modes[0]);
                final int b = getParameter(program, ip + 2, modes[1]);
                final int c = a * b;
                program[program[ip + 3]] = c;
                return ip + 4;
            }
        },
        WRITE(3) {
            @Override
            int execute(final int[] program, final int ip, final int mode) {
                program[program[ip + 1]] = 5;
                return ip + 2;
            }
        },
        READ(4) {
            @Override
            int execute(final int[] program, final int ip, final int mode) {
                final int output = getParameter(program, ip + 1, getModes(mode, 1)[0]);
                final int nextIp = ip + 2;
//                if (output != 0) {
                    if (program[nextIp] == 99) {
                        System.out.println(output);
                    } else {
                        System.err.println(output);
                    }
//                }
                return nextIp;
            }
        },
        JUMP_IF_TRUE(5) {
            @Override
            int execute(final int[] program, final int ip, final int mode) {
                final int[] modes = getModes(mode, 2);
                return getParameter(program, ip + 1, modes[0]) != 0
                        ? getParameter(program, ip + 2, modes[1])
                        : ip + 3;
            }
        },
        JUMP_IF_FALSE(6) {
            @Override
            int execute(final int[] program, final int ip, final int mode) {
                final int[] modes = getModes(mode, 2);
                return getParameter(program, ip + 1, modes[0]) == 0
                        ? getParameter(program, ip + 2, modes[1])
                        : ip + 3;
            }
        },
        LESS_THAN(7) {
            @Override
            int execute(final int[] program, final int ip, final int mode) {
                final int[] modes = getModes(mode, 3);
                final int a = getParameter(program, ip + 1, modes[0]);
                final int b = getParameter(program, ip + 2, modes[1]);
                program[program[ip + 3]] = a < b ? 1 : 0;
                return ip + 4;
            }
        },
        EQUALS(8) {
            @Override
            int execute(final int[] program, final int ip, final int mode) {
                final int[] modes = getModes(mode, 3);
                final int a = getParameter(program, ip + 1, modes[0]);
                final int b = getParameter(program, ip + 2, modes[1]);
                program[program[ip + 3]] = a == b ? 1 : 0;
                return ip + 4;
            }
        };

        private static final OpCode[] lookupTable = OpCode.values();

        private final int opCode;

        OpCode(final int opCode) {
            this.opCode = opCode;
        }

        abstract int execute(final int[] program, final int ip, final int mode);

        static OpCode get(final int opCode) {
            return lookupTable[opCode - 1];
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


    public static void main(String[] args) throws URISyntaxException, IOException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());
        final String[] strings = Files.readString(path, StandardCharsets.ISO_8859_1).trim().split(",");
        final int[] program = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            program[i] = Integer.parseInt(strings[i]);
        }

        int ip = 0;
        while (program[ip] != 99) {
            final int opcode = program[ip] % 100;
            final int mode = program[ip] / 100;
            ip = OpCode.get(opcode).execute(program, ip, mode);
        }
    }
}
