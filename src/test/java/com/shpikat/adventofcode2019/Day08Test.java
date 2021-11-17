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

@DisplayName("Day 08")
public class Day08Test {
    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What is the final image")
        @ParameterizedTest(name = "{0}")
        @MethodSource("testCases")
        void testSolution(String input, int width, int height, String answer) {
            assertEquals(answer, Day08.Part2.solve(input, width, height));
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(named("sample", "0222112222120000"), 2, 2, """
                             $
                            $\040
                            """),
                    arguments(named("puzzle input", readInput("day08_input.txt")), 25, 6, """
                            $$$$ $   $$$$  $    $  $\040
                               $ $   $$  $ $    $  $\040
                              $   $ $ $$$  $    $$$$\040
                             $     $  $  $ $    $  $\040
                            $      $  $  $ $    $  $\040
                            $$$$   $  $$$  $$$$ $  $\040
                            """)
            );
        }
    }
}
