package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Day 19")
public class Day19Test {

    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("How many points are affected by the tractor beam")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            assertEquals(217, Day19.Part1.solve(readInput("day19_input.txt")));
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What are the coordinates to fit in the Santa's ship")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            assertEquals(6840937, Day19.Part2.solve(readInput("day19_input.txt")));
        }
    }
}
