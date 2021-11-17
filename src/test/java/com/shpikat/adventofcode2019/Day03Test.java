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

@DisplayName("Day 03")
public class Day03Test {
    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What is the fewest combined steps the wires must take to reach an intersection")
        @ParameterizedTest(name = "{0} - {1} steps")
        @MethodSource("testCases")
        void testSolution(String input, int answer) {
            assertEquals(answer, Day03.Part2.solve(input));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(named("sample 1", """
                            R75,D30,R83,U83,L12,D49,R71,U7,L72
                            U62,R66,U55,R34,D71,R55,D58,R83
                            """), 610),
                    arguments(named("sample 2", """
                            R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
                            U98,R91,D20,R16,D67,R40,U7,R15,U6,R7
                            """), 410),
                    arguments(named("puzzle input", readInput("day03_input.txt")), 21666)
            );
        }
    }
}
