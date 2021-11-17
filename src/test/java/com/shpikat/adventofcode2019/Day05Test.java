package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Day 05")
public class Day05Test {
    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What is the output")
        @ParameterizedTest(name = "{0} for input value {1} the output value is {2}")
        @MethodSource("testCases")
        void testSolution(String input, int inputValue, int answer) {
            assertEquals(answer, Day05.Part2.solve(input, inputValue));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(named("using position mode", "3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9"), 0, 0),
                    arguments(named("using position mode", "3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9"), -1, 1),
                    arguments(named("using immediate mode", "3,3,1105,-1,9,1101,0,0,12,4,12,99,1"), 0, 0),
                    arguments(named("using immediate mode", "3,3,1105,-1,9,1101,0,0,12,4,12,99,1"), -1, 1),
                    arguments(named("using sample", "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20," +
                            "31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20," +
                            "1105,1,46,98,99"), 7, 999),
                    arguments(named("using sample", "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20," +
                            "31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20," +
                            "1105,1,46,98,99"), 8, 1000),
                    arguments(named("using sample", "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20," +
                            "31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20," +
                            "1105,1,46,98,99"), 9, 1001),
                    arguments(named("using puzzle input", readInput("day05_input.txt")), 5, 12648139)
            );
        }
    }
}
