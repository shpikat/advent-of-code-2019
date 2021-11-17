package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Day 01")
public class Day01Test {
    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("Verify fuel requirements samples")
        @ParameterizedTest(name = "Mass {0} requires {1} fuel")
        @MethodSource("testCases")
        void testCalculateFuel(int mass, int answer) {
            assertEquals(answer, Day01.Part2.calculateFuel(mass));
        }

        @DisplayName("What is the sum of the fuel requirements")
        @Test
        void testSolution() throws IOException {
            assertEquals(4812287, Day01.Part2.solve(readInput("day01_input.txt")));
        }

        private static Stream<Arguments> testCases() {
            return Stream.of(
                    arguments(14, 2),
                    arguments(1969, 966),
                    arguments(100756, 50346)
            );
        }
    }
}
