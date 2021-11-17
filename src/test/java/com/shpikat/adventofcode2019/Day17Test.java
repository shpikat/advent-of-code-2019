package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Day 17")
public class Day17Test {

    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("What is the sum of the alignment parameters")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            assertEquals(11372, Day17.Part1.solve(readInput("day17_input.txt")));
        }
    }
}
