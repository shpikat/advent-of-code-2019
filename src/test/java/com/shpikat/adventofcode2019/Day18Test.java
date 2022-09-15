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

@DisplayName("Day 18")
public class Day18Test {

    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("How many steps is the shortest path that collects all of the keys")
        @ParameterizedTest(name = "{0} - {1} steps")
        @MethodSource("testCases")
        void testSolution(String input, int answer) {
            assertEquals(answer, Day18.Part1.solve(input));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(named("sample 1", """
                            #########
                            #b.A.@.a#
                            #########
                            """), 8),
                    arguments(named("sample 2", """
                            ########################
                            #f.D.E.e.C.b.A.@.a.B.c.#
                            ######################.#
                            #d.....................#
                            ########################
                            """), 86),
                    arguments(named("sample 3", """
                            ########################
                            #...............b.C.D.f#
                            #.######################
                            #.....@.a.B.c.d.A.e.F.g#
                            ########################
                            """), 132),
                    arguments(named("sample 4", """
                            #################
                            #i.G..c...e..H.p#
                            ########.########
                            #j.A..b...f..D.o#
                            ########@########
                            #k.E..a...g..B.n#
                            ########.########
                            #l.F..d...h..C.m#
                            #################
                            """), 136),
                    arguments(named("sample 5", """
                            ########################
                            #@..............ac.GI.b#
                            ###d#e#f################
                            ###A#B#C################
                            ###g#h#i################
                            ########################
                            """), 81),
                    arguments(named("puzzle input", readInput("day18_input.txt")), 3270)
            );
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What is the fewest steps necessary to collect all of the keys")
        @ParameterizedTest(name = "{0} - {1} steps")
        @MethodSource("testCases")
        void testSolution(String input, int answer) {
            assertEquals(answer, Day18.Part2.solve(input));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(named("sample 1", """
                            #######
                            #a.#Cd#
                            ##...##
                            ##.@.##
                            ##...##
                            #cB#Ab#
                            #######
                            """), 8),
                    arguments(named("sample 2", """
                            ###############
                            #d.ABC.#.....a#
                            ######...######
                            ######.@.######
                            ######...######
                            #b.....#.....c#
                            ###############
                            """), 24),
                    arguments(named("sample 3", """
                            #############
                            #DcBa.#.GhKl#
                            #.###...#I###
                            #e#d#.@.#j#k#
                            ###C#...###J#
                            #fEbA.#.FgHi#
                            #############
                            """), 32),
                    arguments(named("sample 4", """
                            #############
                            #g#f.D#..h#l#
                            #F###e#E###.#
                            #dCba...BcIJ#
                            #####.@.#####
                            #nK.L...G...#
                            #M###N#H###.#
                            #o#m..#i#jk.#
                            #############
                            """), 72),
                    arguments(named("puzzle input", readInput("day18_input.txt")), 1628)
            );
        }
    }
}
