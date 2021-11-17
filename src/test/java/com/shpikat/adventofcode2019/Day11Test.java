package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Day 11")
public class Day11Test {

    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("How many panels does it paint at least once")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            assertEquals(2319, Day11.Part1.solve(readInput("day11_input.txt")));
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What registration identifier does it paint")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            final String answer = """
                     $  $ $$$$ $$$  $$$  $$$  $$$$  $$    $$  \040
                     $  $ $    $  $ $  $ $  $ $    $  $    $  \040
                     $  $ $$$  $  $ $  $ $  $ $$$  $       $  \040
                     $  $ $    $$$  $$$  $$$  $    $ $$    $  \040
                     $  $ $    $ $  $    $ $  $    $  $ $  $  \040
                      $$  $$$$ $  $ $    $  $ $     $$$  $$   \040
                    """;
            assertEquals(answer, Day11.Part2.solve(readInput("day11_input.txt")));
        }
    }
}
