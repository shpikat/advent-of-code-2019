package com.shpikat.adventofcode2019.day07;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        final Path path = Paths.get(Main.class.getResource("input.txt").toURI());
        final String[] strings = Files.readString(path, StandardCharsets.ISO_8859_1).trim().split(",");
        final int[] program = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            program[i] = Integer.parseInt(strings[i]);
        }

        final List<Integer[]> allSequences = createPermutations();
        final ExecutorService executorService = Executors.newFixedThreadPool(5);
        final BlockingQueue<Integer> outputA = new ArrayBlockingQueue<>(10);
        final BlockingQueue<Integer> outputB = new ArrayBlockingQueue<>(10);
        final BlockingQueue<Integer> outputC = new ArrayBlockingQueue<>(10);
        final BlockingQueue<Integer> outputD = new ArrayBlockingQueue<>(10);
        final BlockingQueue<Integer> outputE = new ArrayBlockingQueue<>(10);

        // Meaningful names for the I/O bindings
        final BlockingQueue<Integer> inputA = outputE;
        final BlockingQueue<Integer> inputB = outputA;
        final BlockingQueue<Integer> inputC = outputB;
        final BlockingQueue<Integer> inputD = outputC;
        final BlockingQueue<Integer> inputE = outputD;

        final Collection<Callable<Void>> amplifiers = Arrays.asList(
                new Intcode(program, inputA, outputA),
                new Intcode(program, inputB, outputB),
                new Intcode(program, inputC, outputC),
                new Intcode(program, inputD, outputD),
                new Intcode(program, inputE, outputE));
        int max = 0;
        for (final Integer[] sequence : allSequences) {
            // settings
            inputA.put(sequence[0]);
            inputB.put(sequence[1]);
            inputC.put(sequence[2]);
            inputD.put(sequence[3]);
            inputE.put(sequence[4]);

            // input
            inputA.put(0);

            final List<Future<Void>> futures = executorService.invokeAll(amplifiers);

            // verify the execution completed without any exceptions
            for (final Future<Void> future : futures) {
                future.get();
            }

            final int result = outputE.remove();
            max = Math.max(max, result);
        }

        executorService.shutdown();
        System.out.println(max);
    }

    // Heap's algorithm non-recursive implementation
    private static List<Integer[]> createPermutations() {
        final Integer[] elements = new Integer[]{5, 6, 7, 8, 9};
        final int nElements = elements.length;
        final List<Integer[]> permutations = new ArrayList<>(120); // 5!

        final int[] indexes = new int[nElements];
        int i = 0;
        permutations.add(elements.clone());
        while (i < nElements) {
            if (indexes[i] < i) {
                swap(elements, (i & 1) == 0 ? 0 : indexes[i], i);
                permutations.add(elements.clone());
                indexes[i]++;
                i = 0;
            } else {
                indexes[i++] = 0;
            }
        }
        return permutations;
    }

    private static <T> void swap(final T[] arr, final int i, final int j) {
        T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
