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

@DisplayName("Day 06")
public class Day06Test {
    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What is the minimum number of orbital transfers")
        @ParameterizedTest(name = "{0} - {1} transfers")
        @MethodSource("testCases")
        void testSolution(String input,  int answer) {
            assertEquals(answer, Day06.Part2.solve(input));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(named("sample", """
                            COM)B
                            B)C
                            C)D
                            D)E
                            E)F
                            B)G
                            G)H
                            D)I
                            E)J
                            J)K
                            K)L
                            K)YOU
                            I)SAN
                            """), 4),
                    arguments(named("puzzle input", readInput("day06_input.txt")), 361)
            );
        }
    }
}
