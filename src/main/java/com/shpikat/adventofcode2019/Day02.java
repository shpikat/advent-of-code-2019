package com.shpikat.adventofcode2019;

import java.util.Arrays;

public class Day02 {
    static class Part2 {
        static int solve(final String input) {
            final int[] source = Arrays
                    .stream(input.split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            for (int noun = 0; noun <= 99; noun++) {
                for (int verb = 0; verb <= 99; verb++) {
                    final int[] program = source.clone();
                    program[1] = noun;
                    program[2] = verb;
                    for (int ip = 0; program[ip] != 99; ip += 4) {
                        final int a = program[program[ip + 1]];
                        final int b = program[program[ip + 2]];
                        final int c = switch (program[ip]) {
                            case 1 -> a + b;
                            case 2 -> a * b;
                            default -> throw new IllegalStateException("Unexpected value: " + program[ip]);
                        };
                        program[program[ip + 3]] = c;

                    }
                    if (program[0] == 19690720) {
                        return 100 * noun + verb;
                    }
                }
            }

            throw new IllegalStateException("Desired output never reached");
        }
    }
}
