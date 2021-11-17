package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Day 02")
public class Day02Test {
    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What is 100 * noun + verb")
        @Test
        void testSolution() throws IOException {
            assertEquals(5064, Day02.Part2.solve(readInput("day02_input.txt")));
        }
    }
}
