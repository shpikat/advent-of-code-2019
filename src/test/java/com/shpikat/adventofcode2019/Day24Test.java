package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
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

@DisplayName("Day 24")
public class Day24Test {

    private static final Named<String> sample1 = named("sample 1", """
            ....#
            #..#.
            #..##
            ..#..
            #....
            """);

    @DisplayName("part 1")
    @Nested
    class Part1Test {

        @DisplayName("What is the biodiversity rating for the first layout that appears twice?")
        @ParameterizedTest(name = "{0} - {1}")
        @MethodSource("testCases")
        void testSolution(String input, int answer) {
            assertEquals(answer, Day24.Part1.solve(input));
        }

        private static Stream<Arguments> testCases() throws IOException {

            return Stream.of(
                    arguments(sample1, 2129920),
                    arguments(named("puzzle input", readInput("day24_input.txt")), 18400821)
            );
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("How many bugs are present?")
        @ParameterizedTest(name = "{0} after {1} minutes - {2}")
        @MethodSource("testCases")
        void testSolution(String input, int minutes, int answer) {
            assertEquals(answer, Day24.Part2.solve(minutes, input));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(sample1, 10, 99),
                    arguments(named("puzzle input", readInput("day24_input.txt")), 200, 1914)
            );
        }
    }
}
