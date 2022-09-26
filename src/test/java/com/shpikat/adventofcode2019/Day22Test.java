package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Day 22")
public class Day22Test {

    @DisplayName("part 1")
    @Nested
    class Part1Test {

        @DisplayName("What is the position of card 2019?")
        @Test
        void testSolution() throws IOException {
            assertEquals(3324, Day22.Part1.solve(readInput("day22_input.txt")));
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What is the card in position 2020?")
        @Test
        void testSolution() throws IOException {
            assertEquals(74132511136410L, Day22.Part2.solve(readInput("day22_input.txt")));
        }
    }
}
