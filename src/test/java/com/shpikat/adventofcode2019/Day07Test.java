package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Day 07")
public class Day07Test {
    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What is the highest signal that can be sent to the thrusters")
        @ParameterizedTest(name = "{0} - {1}")
        @MethodSource("testCases")
        void testSolution(String input, int answer) throws ExecutionException, InterruptedException {
            assertEquals(answer, Day07.Part2.solve(input));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(named("sample 1", "3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4," +
                            "27,1001,28,-1,28,1005,28,6,99,0,0,5"), 139629729),
                    arguments(named("sample 2", "3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005" +
                            ",55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53," +
                            "1001,56,-1,56,1005,56,6,99,0,0,0,0,10"), 18216),
                    arguments(named("puzzle input", readInput("day07_input.txt")), 6489132)
            );
        }
    }
}
