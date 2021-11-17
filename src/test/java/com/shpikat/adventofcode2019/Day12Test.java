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

@DisplayName("Day 12")
public class Day12Test {

    private static final Named<String> sample1 = named("sample 1", """
            <x=-1, y=0, z=2>
            <x=2, y=-10, z=-7>
            <x=4, y=-8, z=8>
            <x=3, y=5, z=-1>
            """);
    private static final Named<String> sample2 = named("sample 2", """
            <x=-8, y=-10, z=0>
            <x=5, y=5, z=10>
            <x=2, y=-7, z=3>
            <x=9, y=-8, z=-3>
            """);

    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("What is the total energy in the system")
        @ParameterizedTest(name = "{0} after {1} steps - {2}")
        @MethodSource("testCases")
        void testSolution(String input, int steps, int answer) {
            assertEquals(answer, Day12.Part1.solve(input, steps));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(sample1, 10, 179),
                    arguments(sample2, 100, 1940),
                    arguments(named("puzzle input", readInput("day12_input.txt")), 1000, 14809)
            );
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("How many steps does it take")
        @ParameterizedTest(name = "{0} - {1}")
        @MethodSource("testCases")
        void testSolution(String input, long answer) {
            assertEquals(answer, Day12.Part2.solve(input));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(sample1, 2772L),
                    arguments(sample2, 4686774924L),
                    arguments(named("puzzle input", readInput("day12_input.txt")), 282270365571288L)
            );
        }
    }
}
