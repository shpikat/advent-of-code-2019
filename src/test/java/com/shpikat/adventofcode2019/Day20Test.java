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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Day 20")
public class Day20Test {

    private static final Named<String> sample1 = named("sample 1", """
                     A          \s
                     A          \s
              #######.######### \s
              #######.........# \s
              #######.#######.# \s
              #######.#######.# \s
              #######.#######.# \s
              #####  B    ###.# \s
            BC...##  C    ###.# \s
              ##.##       ###.# \s
              ##...DE  F  ###.# \s
              #####    G  ###.# \s
              #########.#####.# \s
            DE..#######...###.# \s
              #.#########.###.# \s
            FG..#########.....# \s
              ###########.##### \s
                         Z      \s
                         Z      \s
            """);

    private static final Named<String> sample2 = named("sample 2", """
                               A              \s
                               A              \s
              #################.############# \s
              #.#...#...................#.#.# \s
              #.#.#.###.###.###.#########.#.# \s
              #.#.#.......#...#.....#.#.#...# \s
              #.#########.###.#####.#.#.###.# \s
              #.............#.#.....#.......# \s
              ###.###########.###.#####.#.#.# \s
              #.....#        A   C    #.#.#.# \s
              #######        S   P    #####.# \s
              #.#...#                 #......VT
              #.#.#.#                 #.##### \s
              #...#.#               YN....#.# \s
              #.###.#                 #####.# \s
            DI....#.#                 #.....# \s
              #####.#                 #.###.# \s
            ZZ......#               QG....#..AS
              ###.###                 ####### \s
            JO..#.#.#                 #.....# \s
              #.#.#.#                 ###.#.# \s
              #...#..DI             BU....#..LF
              #####.#                 #.##### \s
            YN......#               VT..#....QG
              #.###.#                 #.###.# \s
              #.#...#                 #.....# \s
              ###.###    J L     J    #.#.### \s
              #.....#    O F     P    #.#...# \s
              #.###.#####.#.#####.#####.###.# \s
              #...#.#.#...#.....#.....#.#...# \s
              #.#####.###.###.#.#.#########.# \s
              #...#.#.....#...#.#.#.#.....#.# \s
              #.###.#####.###.###.#.#.####### \s
              #.#.........#...#.............# \s
              #########.###.###.############# \s
                       B   J   C              \s
                       U   P   P              \s
            """);

    @DisplayName("part 1")
    @Nested
    class Part1Test {

        @DisplayName("How many steps how many steps does it take to get from AA to ZZ?")
        @ParameterizedTest(name = "{0} - {1} steps")
        @MethodSource("testCases")
        void testSolution(String input, int answer) {
            assertEquals(answer, Day20.Part1.solve(input));
        }

        private static Stream<Arguments> testCases() throws IOException {

            return Stream.of(
                    arguments(sample1, 23),
                    arguments(sample2, 58),
                    arguments(named("puzzle input", readInput("day20_input.txt")), 578)
            );
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("How many steps how many steps does it take to get from AA to ZZ, both at the outermost layer?")
        @ParameterizedTest(name = "{0} - {1} steps")
        @MethodSource("testCases")
        void testSolution(String input, int answer) {
            if (answer < 0) {
                assertThrows(IllegalArgumentException.class, () -> Day20.Part2.solve(input));
            } else {
                assertEquals(answer, Day20.Part2.solve(input));
            }
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(sample1, 26),
                    arguments(sample2, -1), // negative value when no solution exists
                    arguments(named("sample 3", """
                                         Z L X W       C                \s
                                         Z P Q B       K                \s
                              ###########.#.#.#.#######.############### \s
                              #...#.......#.#.......#.#.......#.#.#...# \s
                              ###.#.#.#.#.#.#.#.###.#.#.#######.#.#.### \s
                              #.#...#.#.#...#.#.#...#...#...#.#.......# \s
                              #.###.#######.###.###.#.###.###.#.####### \s
                              #...#.......#.#...#...#.............#...# \s
                              #.#########.#######.#.#######.#######.### \s
                              #...#.#    F       R I       Z    #.#.#.# \s
                              #.###.#    D       E C       H    #.#.#.# \s
                              #.#...#                           #...#.# \s
                              #.###.#                           #.###.# \s
                              #.#....OA                       WB..#.#..ZH
                              #.###.#                           #.#.#.# \s
                            CJ......#                           #.....# \s
                              #######                           ####### \s
                              #.#....CK                         #......IC
                              #.###.#                           #.###.# \s
                              #.....#                           #...#.# \s
                              ###.###                           #.#.#.# \s
                            XF....#.#                         RF..#.#.# \s
                              #####.#                           ####### \s
                              #......CJ                       NM..#...# \s
                              ###.#.#                           #.###.# \s
                            RE....#.#                           #......RF
                              ###.###        X   X       L      #.#.#.# \s
                              #.....#        F   Q       P      #.#.#.# \s
                              ###.###########.###.#######.#########.### \s
                              #.....#...#.....#.......#...#.....#.#...# \s
                              #####.#.###.#######.#######.###.###.#.#.# \s
                              #.......#.......#.#.#.#.#...#...#...#.#.# \s
                              #####.###.#####.#.#.#.#.###.###.#.###.### \s
                              #.......#.....#.#...#...............#...# \s
                              #############.#.#.###.################### \s
                                           A O F   N                    \s
                                           A A D   M                    \s
                            """), 396),
                    arguments(named("puzzle input", readInput("day20_input.txt")), 6592)
            );
        }
    }
}
