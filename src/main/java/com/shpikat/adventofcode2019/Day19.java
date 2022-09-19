package com.shpikat.adventofcode2019;

import java.util.concurrent.ExecutionException;

public class Day19 {

    static class Part1 {

        private static final int AREA_SIZE = 50;

        static int solve(final String input) throws InterruptedException, ExecutionException {
            // Capacity must be big enough
            final Intcode computer = Intcode.fromInput(input, 16 * 1024);

            final BeamTracker tracker = new BeamTracker(computer);

            return tracker.calculatePointsInArea(AREA_SIZE);
        }
    }

    static class Part2 {

        private static final int SHIP_SIZE = 100;

        static int solve(final String input) throws InterruptedException, ExecutionException {
            // Capacity must be big enough
            final Intcode computer = Intcode.fromInput(input, 16 * 1024);

            final BeamTracker tracker = new BeamTracker(computer);

            return tracker.findFit(SHIP_SIZE);
        }
    }

    private static class BeamTracker {
        private final Intcode computer;

        public BeamTracker(final Intcode computer) {
            this.computer = computer;
        }

        public int calculatePointsInArea(final int size) throws InterruptedException {
            int beamStartX = 1; // (0, 0) is always pulled, we can skip it
            int beamStartY = 0;
            int counter = 0;

            // Find the start of the beam to work with the special case, when the beam isn't contiguous and there's a
            // gap between the emitter and the beam itself, like this:
            // #..............
            // ...............
            // ...............
            // ..#............
            // ...#...........
            // ....#..........
            // ....##.........
            // .....#.........
            // ......#........
            // ......##.......
            // .......##......
            // ........##.....
            // ........###....
            // .........##....
            // ..........##...
            if (!isPulled(beamStartX, beamStartY)) {
                counter++; // Count the emitter in
                do {
                    beamStartX = 0;
                    beamStartY++;
                    while (beamStartX < size && !isPulled(beamStartX, beamStartY)) {
                        beamStartX++;
                    }
                } while (beamStartX == size);
            }

            int right = beamStartX;
            int left = beamStartX;

            for (int y = beamStartY; y < size; y++) {
                while (right < size && isPulled(right, y)) {
                    right++;
                }
                while (left < size && !isPulled(left, y)) {
                    left++;
                }
                counter += right - left;
            }

            return counter;
        }

        public int findFit(final int size) throws InterruptedException {

            // See calculatePointsInArea for comments, as feels too complicated to refactor the common code here.

            int beamStartX = 1;
            int beamStartY = 0;

            final int startingSearchArea = Math.max(10, size);
            if (!isPulled(beamStartX, beamStartY)) {
                do {
                    beamStartX = 0;
                    beamStartY++;
                    while (beamStartX < startingSearchArea && !isPulled(beamStartX, beamStartY)) {
                        beamStartX++;
                    }
                } while (beamStartX == startingSearchArea);
            }

            int right = beamStartX;
            int top = beamStartY;
            while (isPulled(right, top)) {
                right++;
            }

            int left = beamStartX;
            int bottom = beamStartY;

            while (right - left < size || bottom - top < size - 1) {
                while (right - left < size) {
                    top++;
                    while (isPulled(right, top)) {
                        right++;
                    }
                }
                while (bottom - top < size - 1) {
                    bottom++;
                    while (!isPulled(left, bottom)) {
                        left++;
                    }
                }
            }

            return left * 10_000 + top;
        }

        private boolean isPulled(final int x, final int y) throws InterruptedException {
            computer.input.put((long) x);
            computer.input.put((long) y);
            computer.call();
            return computer.output.take() != 0L;
        }
    }
}
