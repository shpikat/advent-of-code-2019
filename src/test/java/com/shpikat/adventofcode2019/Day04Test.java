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

@DisplayName("Day 04")
public class Day04Test {
    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("Verify password samples")
        @ParameterizedTest(name = "password {0} is good: {1}")
        @MethodSource("testCases")
        void testCalculateFuel(String password, boolean answer) {
            assertEquals(answer, Day04.Part2.isPasswordGood(password.toCharArray()));
        }

        @DisplayName("How many different passwords")
        @Test
        void testSolution() throws IOException {
            assertEquals(710, Day04.Part2.solve(readInput("day04_input.txt")));
        }

        private static Stream<Arguments> testCases() {
            return Stream.of(
                    arguments("112233", true),
                    arguments("123444", false),
                    arguments("111122", true)
            );
        }
    }
}
