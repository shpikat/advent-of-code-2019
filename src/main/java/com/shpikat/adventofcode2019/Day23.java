package com.shpikat.adventofcode2019;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Day23 {

    static class Part1 {

        static int solve(final String input) throws InterruptedException {
            final Network network = new Network(input);
            return network.bootUp(false);
        }
    }

    static class Part2 {

        static int solve(final String input) throws InterruptedException {
            final Network network = new Network(input);
            return network.bootUp(true);
        }
    }

    private static class Network {
        private static final int SIZE = 50;

        private final ExecutorService executor = Executors.newFixedThreadPool(SIZE);

        private final Intcode[] nics;

        Network(final String input) {
            this.nics = new Intcode[SIZE];
            for (int i = 0; i < SIZE; i++) {
                this.nics[i] = Intcode.fromInput(input, 32);
            }
        }

        int bootUp(boolean isNatEnabled) throws InterruptedException {
            for (int i = 0; i < nics.length; i++) {
                final Intcode nic = nics[i];
                executor.submit(nic);
                nic.input.put((long) i);
            }

            Integer result = null;
            Long lastNatX = null;
            Long lastNatY = null;
            Long previousLastNatY = null;
            int idleCycles = 0;
            while (result == null) {
                idleCycles++;
                for (final Intcode nic : nics) {
                    final Long value = nic.output.poll();
                    if (value != null) {
                        idleCycles = 0;

                        final int address = value.intValue();
                        final Long x = nic.output.take();
                        final Long y = nic.output.take();
                        if (address == 255) {
                            if (isNatEnabled) {
                                lastNatX = x;
                                lastNatY = y;
                            } else {
                                result = Math.toIntExact(y);
                            }
                        } else {
                            final Intcode destination = nics[address];
                            final Long peek = destination.input.peek();
                            if (peek != null && peek == -1L) {
                                final Long head = destination.input.poll();
                                assert head == null || head == -1L;
                            }
                            destination.input.put(x);
                            destination.input.put(y);
                        }
                    }
                }
                if (idleCycles > 4) {
                    // Most probably there's thread starvation for NIC processing.
                    // Let some threads block waiting for the input to let other threads make progress.
                    for (final Intcode nic : nics) {
                        if (nic.input.isEmpty()) {
                            nic.input.put(-1L);
                        }
                    }
                }
                if (isNatEnabled && lastNatX != null && idleCycles > 200) {
                    final Long peek = nics[0].input.peek();
                    if (peek != null && peek == -1L) {
                        final Long head = nics[0].input.poll();
                        assert head == null || head == -1L;
                    }
                    nics[0].input.put(lastNatX);
                    nics[0].input.put(lastNatY);
                    if (previousLastNatY != null && previousLastNatY.equals(lastNatY)) {
                        result = Math.toIntExact(previousLastNatY);
                    } else {
                        previousLastNatY = lastNatY;
                    }
                    // Let other threads handle the wake-up burst
                    Thread.sleep(10L);
                }
            }
            executor.shutdownNow();
            return result;
        }
    }
}
