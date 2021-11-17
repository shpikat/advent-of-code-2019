package com.shpikat.adventofcode2019;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Day17 {

    static class Part1 {
        static int solve(final String input) throws InterruptedException {
            final Day09.Intcode computer = Day09.Intcode.fromInput(input, 10);

            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<Void> future = executor.submit(computer);

            final List<Long> buffer = new ArrayList<>(1000);
            do {
                if (computer.output.drainTo(buffer) < 100) {
                    Thread.sleep(50);
                }
            } while (!future.isDone());
            computer.output.drainTo(buffer);

            final StringBuilder viewBiulder = new StringBuilder(buffer.size());
            for (final Long symbol : buffer) {
                viewBiulder.append((char) symbol.intValue());
            }

            int sum = 0;
            final String[] lines = viewBiulder.toString().split("\n");
            for (int i = 1; i < lines.length - 1; i++) {
                final String line = lines[i];
                for (int j = 1; j < line.length() - 1; j++) {
                    if (line.charAt(j - 1) == '#'
                            && line.charAt(j + 1) == '#'
                            && lines[i - 1].charAt(j) == '#'
                            && lines[i + 1].charAt(j) == '#') {
                        sum += i * j;
                    }
                }
            }
            executor.shutdown();

            return sum;
        }
    }
}
