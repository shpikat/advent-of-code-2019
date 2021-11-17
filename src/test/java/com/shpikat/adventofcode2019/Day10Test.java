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

@DisplayName("Day 10")
public class Day10Test {

    private static final String SAMPLE_4 = """
            .#..##.###...#######
            ##.############..##.
            .#.######.########.#
            .###.#######.####.#.
            #####.##.#.##.###.##
            ..#####..#.#########
            ####################
            #.####....###.#.#.##
            ##.#################
            #####.##.###..####..
            ..######..##.#######
            ####.##.####...##..#
            .#####..#.######.###
            ##...#.##########...
            #.##########.#######
            .####.#.###.###.#.##
            ....##.##.###..#####
            .#.#.###########.###
            #.#.#.#####.####.###
            ###.##.####.##.#..##
            """;

    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("How many other asteroids can be detected from that location")
        @ParameterizedTest(name = "{0} - {1}")
        @MethodSource("testCases")
        void testSolution(String input, int answer) throws InterruptedException {
            assertEquals(answer, Day10.Part1.solve(input));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(named("sample 1", """
                            .#..#
                            .....
                            #####
                            ....#
                            ...##
                            """), 8),
                    arguments(named("sample 2", """
                            ......#.#.
                            #..#.#....
                            ..#######.
                            .#.#.###..
                            .#..#.....
                            ..#....#.#
                            #..#....#.
                            .##.#..###
                            ##...#..#.
                            .#....####
                            """), 33),
                    arguments(named("sample 3", """
                            #.#...#.#.
                            .###....#.
                            .#....#...
                            ##.#.#.#.#
                            ....#.#.#.
                            .##..###.#
                            ..#...##..
                            ..##....##
                            ......#...
                            .####.###.
                            """), 35),
                    arguments(named("sample 4", """
                            .#..#..###
                            ####.###.#
                            ....###.#.
                            ..###.##.#
                            ##.##.#.#.
                            ....###..#
                            ..#.#..#.#
                            #..#.#.###
                            .##...##.#
                            .....#.#..
                            """), 41),
                    arguments(named("sample 4", SAMPLE_4), 210),
                    arguments(named("puzzle input", readInput("day10_input.txt")), 326)
            );
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("Coordinates of the 200th asteroid to be vaporized")
        @ParameterizedTest(name = "{0} - {1}")
        @MethodSource("testCases")
        void testSolution(String input, int answer) throws InterruptedException {
            assertEquals(answer, Day10.Part2.solve(input));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(named("sample", SAMPLE_4), 802),
                    arguments(named("puzzle input", readInput("day10_input.txt")), 1623)
            );
        }
    }
}
