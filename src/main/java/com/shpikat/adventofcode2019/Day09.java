package com.shpikat.adventofcode2019;

import java.util.ArrayList;
import java.util.List;

public class Day09 {
    static class Part1 {
        static List<Long> solve(final String input) throws InterruptedException {
            return Day09.solve(input, 1L);
        }
    }

    static class Part2 {
        static List<Long> solve(final String input) throws InterruptedException {
            return Day09.solve(input, 2L);
        }
    }

    private static List<Long> solve(final String input, final long inputValue) throws InterruptedException {
        final Intcode computer = Intcode.fromInput(input, 32);
        computer.input.put(inputValue);
        computer.call();

        final List<Long> result = new ArrayList<>(computer.output.size());
        computer.output.drainTo(result);
        return result;
    }
}
