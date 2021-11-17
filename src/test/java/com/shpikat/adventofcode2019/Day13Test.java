package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Day 13")
public class Day13Test {

    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("How many block tiles are on the screen when the game exits")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            assertEquals(318, Day13.Part1.solve(readInput("day13_input.txt")));
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What is your score after the last block is broken")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            assertEquals(16309, Day13.Part2.solve(readInput("day13_input.txt")));
        }
    }
}
