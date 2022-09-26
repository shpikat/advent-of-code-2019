package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Day 23")
public class Day23Test {

    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("What is the Y value of the first packet sent to address 255?")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            assertEquals(26744, Day23.Part1.solve(readInput("day23_input.txt")));
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What is the first Y value delivered by the NAT to the computer at address 0 twice in a row?")
        @Test
        void testSolution() throws IOException, ExecutionException, InterruptedException {
            assertEquals(19498, Day23.Part2.solve(readInput("day23_input.txt")));
        }
    }
}
