package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Day 21")
public class Day21Test {

    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("What is the amount of hull damage?")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            assertEquals(19352720, Day21.Part1.solve(readInput("day21_input.txt")));
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What is the amount of hull damage?")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            assertEquals(1143652885, Day21.Part2.solve(readInput("day21_input.txt")));
        }
    }
}
