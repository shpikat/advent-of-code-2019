package com.shpikat.adventofcode2019;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.shpikat.adventofcode2019.Utils.readInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Day 09")
public class Day09Test {
    @DisplayName("part 1")
    @Nested
    class Part1Test {
        @DisplayName("What is the BOOST keycode")
        @ParameterizedTest(name = "{0} - {1}")
        @MethodSource("testCases")
        void testSolution(String input, String answer) throws InterruptedException {
            final String actual = Day09.Part1.solve(input)
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
            assertEquals(answer, actual);
        }

        private static Stream<Arguments> testCases() throws IOException {
            return Stream.of(
                    arguments(
                            named("sample 1", "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99"),
                            named("a copy of itself", "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99")),
                    arguments(
                            named("sample 2", "1102,34915192,34915192,7,4,7,99,0"),
                            named("a 16-digit number", "1219070632396864")),
                    arguments(
                            named("sample 3", "104,1125899906842624,99"),
                            named("the large number in the middle", "1125899906842624")),
                    arguments(named("puzzle input", readInput("day09_input.txt")), "2465411646")
            );
        }
    }

    @DisplayName("part 2")
    @Nested
    class Part2Test {
        @DisplayName("What are the coordinates of the distress signal")
        @Test
        void testSolution() throws InterruptedException, IOException {
            assertEquals(Collections.singletonList(69781L), Day09.Part2.solve(readInput("day09_input.txt")));
        }
    }
}
