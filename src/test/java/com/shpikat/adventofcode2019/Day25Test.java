package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Day 25")
public class Day25Test {

    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("What is the password for the main airlock?")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            assertEquals(529920, Day25.Part1.solve(readInput("day25_input.txt")));
        }
    }
}
