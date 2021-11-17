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

@DisplayName("Day 16")
public class Day16Test {

    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("What are the first eight digits in the final output list")
        @ParameterizedTest(name = "{0} after {1} phases - {2}")
        @MethodSource("testCases")
        void testSolution(String input, int phases, String answer) {
            assertEquals(answer, Day16.Part1.solve(input, phases));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(named("sample 1", "12345678"), 4, "01029498"),
                    arguments(named("sample 2", "80871224585914546619083218645595"), 100, "24176176"),
                    arguments(named("sample 3", "19617804207202209144916044189917"), 100, "73745418"),
                    arguments(named("sample 4", "69317163492948606335995924319873"), 100, "52432133"),
                    arguments(named("puzzle input", readInput("day16_input.txt")), 100, "37153056")
            );
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What is the eight-digit message embedded in the final output list")
        @ParameterizedTest(name = "{0} - {1}")
        @MethodSource("testCases")
        void testSolution(String input, String answer) {
            assertEquals(answer, Day16.Part2.solve(input));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(named("sample 1", "03036732577212944063491565474664"), "84462026"),
                    arguments(named("sample 2", "02935109699940807407585447034323"), "78725270"),
                    arguments(named("sample 3", "03081770884921959731165446850517"), "53553731"),
                    arguments(named("puzzle input", readInput("day16_input.txt")), "60592199")
            );
        }
    }
}
