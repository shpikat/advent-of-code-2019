package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Day 15")
public class Day15Test {

    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("What is the fewest number of movement commands")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            assertEquals(412, Day15.Part1.solve(readInput("day15_input.txt")));
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("How many minutes will it take to fill with oxygen")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            assertEquals(418, Day15.Part2.solve(readInput("day15_input.txt")));
        }
    }
}
