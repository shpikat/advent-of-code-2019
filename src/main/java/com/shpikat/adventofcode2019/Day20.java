package com.shpikat.adventofcode2019;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Day20 {

    private static final char EMPTY = ' ';
    private static final char PASSAGE = '.';
    private static final String START = "AA";
    private static final String END = "ZZ";

    private static final Coordinate[] neighbourhood = new Coordinate[]{
            new Coordinate(0, -1),
            new Coordinate(-1, 0),
            new Coordinate(+1, 0),
            new Coordinate(0, +1),
    };

    static class Part1 {
        static int solve(final String input) {
            final String[] lines = input.split("\n");

            final Map<String, Coordinate> outerPortals = findOuterPortals(lines);
            final Map<String, Coordinate> innerPortals = findInnerPortals(lines);

            // Start is not re-entrant, we are safe to pretend it doesn't exist after we are in
            final Coordinate start = outerPortals.remove(START);

            final Map<Coordinate, String> portalEntries = new HashMap<>();
            portalEntries.putAll(getPortalEntries(outerPortals));
            portalEntries.putAll(getPortalEntries(innerPortals));

            final Map<Coordinate, Integer> shortestPaths = new HashMap<>();
            final Queue<State> queue = new PriorityQueue<>(Comparator.comparingInt(State::steps));
            queue.offer(new State(start, 0));

            while (!queue.isEmpty()) {
                final State current = queue.poll();
                assert current != null;
                final int steps = shortestPaths.compute(
                        current.coordinate(),
                        (c, old) -> old == null || current.steps() < old ? current.steps() : old
                );
                if (steps == current.steps()) {
                    for (final Coordinate neighbour : neighbourhood) {
                        final Coordinate next = current.coordinate().add(neighbour);
                        if (next.getValueFromGrid(lines) == PASSAGE) {
                            final String portal = portalEntries.get(next);
                            if (portal == null) {
                                queue.add(new State(next, current.steps() + 1));
                            } else if (portal.equals(END)) {
                                return current.steps() + 1;
                            } else {
                                Coordinate coordinate = outerPortals.get(portal);
                                if (coordinate.equals(next)) {
                                    coordinate = innerPortals.get(portal);
                                }
                                queue.add(new State(coordinate, current.steps() + 2));
                            }
                        }
                    }
                }
            }

            throw new IllegalArgumentException("Input has no solution");
        }

        private record State(Coordinate coordinate, int steps) {
        }
    }

    static class Part2 {
        static int solve(final String input) {
            final String[] lines = input.split("\n");

            final Map<String, Coordinate> outerPortals = findOuterPortals(lines);
            final Map<String, Coordinate> innerPortals = findInnerPortals(lines);

            // Start is not re-entrant, we are safe to pretend it doesn't exist after we are in
            final Coordinate start = outerPortals.remove(START);

            final Map<Coordinate, String> outerPortalEntries = getPortalEntries(outerPortals);
            final Map<Coordinate, String> innerPortalEntries = getPortalEntries(innerPortals);

            // This part would benefit greatly from using the weighted graph. Although with the established level limit
            // (see below), the speed is good enough as it is.

            final Map<Integer, Map<Coordinate, Integer>> shortestPaths = new HashMap<>();
            final Queue<State> queue = new PriorityQueue<>(Comparator.comparingInt(State::steps));
            queue.offer(new State(start, 0, 0));

            while (!queue.isEmpty()) {
                final State current = queue.poll();
                assert current != null;
                final int steps = shortestPaths
                        .computeIfAbsent(current.level(), level -> new HashMap<>())
                        .compute(
                                current.coordinate(),
                                (c, old) -> old == null || current.steps() < old ? current.steps() : old
                        );
                if (steps == current.steps()) {
                    for (final Coordinate neighbour : neighbourhood) {
                        final Coordinate next = current.coordinate().add(neighbour);
                        if (next.getValueFromGrid(lines) == PASSAGE) {
                            final String outerPortal = outerPortalEntries.get(next);
                            if (outerPortal == null) {
                                final String innerPortal = innerPortalEntries.get(next);
                                if (innerPortal == null) {
                                    queue.add(new State(next, current.level(), current.steps() + 1));
                                } else {
                                    // A simple, yet extremely clever idea (borrowed) for speeding up complicated solutions
                                    if (current.level() < innerPortals.size()) {
                                        queue.add(new State(outerPortals.get(innerPortal), current.level() + 1, current.steps() + 2));
                                    }
                                }
                            } else if (outerPortal.equals(END)) {
                                if (current.level() == 0) {
                                    return current.steps() + 1;
                                }
                            } else if (current.level() != 0) {
                                queue.add(new State(innerPortals.get(outerPortal), current.level() - 1, current.steps() + 2));
                            }
                        }
                    }
                }
            }

            throw new IllegalArgumentException("Input has no solution");
        }

        private record State(Coordinate coordinate, int level, int steps) {
        }
    }

    private static Map<String, Coordinate> findOuterPortals(final String[] grid) {
        final Map<String, Coordinate> portals = new HashMap<>();

        for (int x = 2; x < grid[0].length() - 2; x++) {
            final char ch = grid[0].charAt(x);
            if (ch != EMPTY) {
                portals.put(new String(new char[]{ch, grid[1].charAt(x)}), new Coordinate(x, 2));
            }
        }
        final int preLastLine = grid.length - 2;
        for (int x = 2; x < grid[preLastLine].length() - 2; x++) {
            final char ch = grid[preLastLine].charAt(x);
            if (ch != EMPTY) {
                portals.put(new String(new char[]{ch, grid[preLastLine + 1].charAt(x)}), new Coordinate(x, preLastLine - 1));
            }
        }
        final int preLastColumn = grid[0].length() - 2;
        for (int y = 2; y < grid.length - 2; y++) {
            final char ch = grid[y].charAt(0);
            if (ch != EMPTY) {
                portals.put(new String(new char[]{ch, grid[y].charAt(1)}), new Coordinate(2, y));
            }
            final char ch2 = grid[y].charAt(preLastColumn);
            if (ch2 != EMPTY) {
                portals.put(new String(new char[]{ch2, grid[y].charAt(preLastColumn + 1)}), new Coordinate(preLastColumn - 1, y));
            }
        }

        return portals;
    }

    private static Map<String, Coordinate> findInnerPortals(final String[] grid) {
        final Map<String, Coordinate> portals = new HashMap<>();

        // Find inner edges
        int left = grid[0].length();
        int top = grid.length;
        int right = 0;
        int bottom = 0;

        final int leftEdge = 2;
        final int rightEdge = grid[0].length() - 3;
        for (int y = 2; y < grid.length - 2; y++) {
            final int index = grid[y].indexOf(EMPTY, leftEdge);
            if (index >= 0 && index <= rightEdge) {
                left = Math.min(left, index);
                top = Math.min(top, y);
                bottom = Math.max(bottom, y);
                right = Math.max(right, grid[y].lastIndexOf(EMPTY, rightEdge));
            }
        }

        for (int x = left; x <= right; x++) {
            final char ch = grid[top].charAt(x);
            if (ch != EMPTY) {
                final char ch2 = grid[top + 1].charAt(x);
                if (ch2 != EMPTY) {
                    portals.put(new String(new char[]{ch, ch2}), new Coordinate(x, top - 1));
                }
            }
        }
        for (int x = left; x <= right; x++) {
            final char ch = grid[bottom - 1].charAt(x);
            if (ch != EMPTY) {
                final char ch2 = grid[bottom].charAt(x);
                if (ch2 != EMPTY) {
                    portals.put(new String(new char[]{ch, ch2}), new Coordinate(x, bottom + 1));
                }
            }
        }
        for (int y = top; y <= bottom; y++) {
            final char chLeft = grid[y].charAt(left);
            if (chLeft != EMPTY) {
                final char chLeft2 = grid[y].charAt(left + 1);
                if (chLeft2 != EMPTY) {
                    portals.put(new String(new char[]{chLeft, chLeft2}), new Coordinate(left - 1, y));
                }
            }
            final char chRight = grid[y].charAt(right - 1);
            if (chRight != EMPTY) {
                final char chRight2 = grid[y].charAt(right);
                if (chRight2 != EMPTY) {
                    portals.put(new String(new char[]{chRight, chRight2}), new Coordinate(right + 1, y));
                }
            }
        }

        return portals;
    }

    private static Map<Coordinate, String> getPortalEntries(final Map<String, Coordinate> portals) {
        final Map<Coordinate, String> portalEntries = new HashMap<>();
        for (final Map.Entry<String, Coordinate> entry : portals.entrySet()) {
            portalEntries.put(entry.getValue(), entry.getKey());
        }
        return portalEntries;
    }

    private record Coordinate(int x, int y) {
        Coordinate add(Coordinate c) {
            return new Coordinate(c.x() + x(), c.y() + y());
        }

        char getValueFromGrid(final String[] grid) {
            return grid[y()].charAt(x());
        }

        int getBitIndex(final int lineLength) {
            return y() * lineLength + x();
        }
    }
}
