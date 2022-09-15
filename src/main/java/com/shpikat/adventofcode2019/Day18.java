package com.shpikat.adventofcode2019;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Day18 {

    private static final char ENTRANCE = '@';
    private static final char PASSAGE = '.';
    private static final char WALL = '#';

    private static final Coordinate[] neighbourhood = new Coordinate[]{
            new Coordinate(0, -1),
            new Coordinate(-1, 0),
            new Coordinate(+1, 0),
            new Coordinate(0, +1),
    };

    static class Part1 {
        static int solve(final String input) {
            final String[] lines = input.split("\n");
            if (lines.length == 0) {
                throw new IllegalArgumentException("No input");
            }

            return findShortestPath(lines, new char[]{ENTRANCE});
        }
    }

    static class Part2 {

        private static final char[] ROBOTS = {'@', '$', '%', '&'};

        private static final String[] mapUpdate = {
                ROBOTS[0] + "#" + ROBOTS[1],
                "###",
                ROBOTS[2] + "#" + ROBOTS[3],
        };

        static int solve(final String input) {
            final String[] lines = input.split("\n");
            if (lines.length == 0) {
                throw new IllegalArgumentException("No input");
            }

            // Update the map
            for (int y = 0; y < lines.length; y++) {
                final int x = lines[y].indexOf(ENTRANCE);
                if (x >= 0) {
                    for (int i = 0; i < mapUpdate.length; i++) {
                        final int y1 = y - 1 + i;
                        lines[y1] = lines[y1].substring(0, x - 1)
                                + mapUpdate[i]
                                + lines[y1].substring(x + 2);
                    }
                    break;
                }
            }

            // A wrong assumption can be made (I made it, too) that the doors with the keys in a different section can
            // be ignored, as the robot won't make any steps waiting at the door until the key is collected elsewhere.
            // Actually the global shortest path can be different from the combination of the local shortest paths.
            //
            // The sample 4 is an example. The sum of the shortest paths will be 70, which is incorrect, as some of
            // those paths will be impossible because of some doors cross-blocking the path.

            return findShortestPath(lines, ROBOTS);
        }
    }

    private static int findShortestPath(final String[] grid, final char[] robots) {
        // micro-optimization
        final int gridWidth = grid[0].length();

        /*
         All the Map<Character, ?> variables can be refactored into arrays for further performance improvements.
         At this moment the solution is sub-second anyway, and readability wins.
         */

        // Consider the keys and the entrances as the graph vertices.
        final Map<Character, Coordinate> nodes = new HashMap<>();
        for (int y = 0; y < grid.length; y++) {
            final String line = grid[y];
            for (int x = 0; x < line.length(); x++) {
                final char ch = line.charAt(x);
                if (ch != PASSAGE && ch != WALL && !isDoor(ch)) {
                    nodes.put(ch, new Coordinate(x, y));
                }
            }
        }


        // Collect the distances between the keys only. The doors on the way become the property of an edge.
        // Entrances are presented as the starting points and excluded as neighbours to minimize the number of
        // transitions during search.
        final Map<Character, Map<Character, Edge>> edges = new HashMap<>();
        for (final Map.Entry<Character, Coordinate> entry : nodes.entrySet()) {
            final Character currentNode = entry.getKey();

            final Map<Character, Edge> adjacent = new HashMap<>();
            edges.put(currentNode, adjacent);

            final Queue<EdgeState> queue = new ArrayDeque<>();
            final BitSet visited = new BitSet();
            int step = 0;
            queue.offer(new EdgeState(entry.getValue(), 0));
            while (!queue.isEmpty()) {
                step++;
                final int size = queue.size();
                for (int i = 0; i < size; i++) {
                    final EdgeState current = queue.poll();
                    assert current != null;
                    final int bitIndex = current.coordinate().getBitIndex(gridWidth);
                    if (!visited.get(bitIndex)) {
                        visited.set(bitIndex);
                        for (final Coordinate delta : neighbourhood) {
                            final Coordinate next = current.coordinate().add(delta);
                            final char ch = next.getValueFromGrid(grid);
                            if (ch != currentNode) {
                                if (isKey(ch)) {
                                    adjacent.put(ch, new Edge(current.requiredKeys(), step));
                                } else if (isDoor(ch)) {
                                    final int requiredKeys = addKey(current.requiredKeys(), (char) (ch - 'A' + 'a'));
                                    queue.offer(new EdgeState(next, requiredKeys));
                                } else if (ch != WALL) {
                                    queue.offer(new EdgeState(next, current.requiredKeys()));
                                }
                            }
                        }
                    }
                }
            }
        }

        // Prepare to stop the robot once he collected all keys in its section.
        final int[] keysInSection = new int[robots.length];
        final Set<Character> visited = new HashSet<>();
        final Queue<RobotIndex> q = new ArrayDeque<>();
        for (int i = 0; i < robots.length; i++) {
            q.offer(new RobotIndex(i, robots[i]));
        }
        while (!q.isEmpty()) {
            final RobotIndex robotIndex = q.poll();
            final int index = robotIndex.index();
            final char node = robotIndex.node();
            if (isKey(node)) {
                keysInSection[index] = addKey(keysInSection[index], node);
            }
            for (final Character next : edges.get(node).keySet()) {
                if (visited.add(next)) {
                    q.offer(new RobotIndex(index, next));
                }
            }
        }
        int allKeys = 0;
        for (final int keys : keysInSection) {
            allKeys |= keys;
        }

        // Multimap of robots to acquired keys to current steps
        final Map<Integer, Map<Integer, Integer>> steps = new HashMap<>();
        final PriorityQueue<RobotsState> queue = new PriorityQueue<>(Comparator.comparingInt(RobotsState::steps));
        queue.offer(new RobotsState(Robots.create(robots), 0, 0));

        while (!queue.isEmpty()) {
            final RobotsState current = queue.poll();
            if (current.keys() == allKeys) {
                return current.steps();
            } else {
                for (int i = 0; i < current.robots().size(); i++) {
                    if (!hasAllKeys(keysInSection[i], current.keys())) {
                        final char robot = current.robots().get(i);
                        for (final Map.Entry<Character, Edge> entry : edges.get(robot).entrySet()) {
                            final Edge edge = entry.getValue();
                            if (hasAllKeys(edge.requiredKeys(), current.keys())) {
                                final char nextNode = entry.getKey();
                                final Robots nextRobots = current.robots().replace(i, nextNode);
                                final int nextKeys = isKey(nextNode) ? addKey(current.keys(), nextNode) : current.keys();
                                final int nextSteps = current.steps() + edge.steps();
                                final Map<Integer, Integer> stepsForState = steps.computeIfAbsent(nextRobots.robots(), k -> new HashMap<>());
                                final Integer previousSteps = stepsForState.get(nextKeys);
                                if (previousSteps == null || previousSteps > nextSteps) {
                                    stepsForState.put(nextKeys, nextSteps);
                                    queue.offer(new RobotsState(nextRobots, nextKeys, nextSteps));
                                }
                            }
                        }
                    }
                }
            }
        }

        throw new IllegalArgumentException("Input has no solution");
    }

    private static boolean isKey(final char ch) {
        return 'a' <= ch && ch <= 'z';
    }

    private static boolean isDoor(final char ch) {
        return 'A' <= ch && ch <= 'Z';
    }

    private static int addKey(final int keys, final char key) {
        return keys | 1 << key - 'a';
    }

    private static boolean hasAllKeys(final int requiredKeys, final int keys) {
        return (requiredKeys & keys) == requiredKeys;
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

    private record EdgeState(Coordinate coordinate, int requiredKeys) {
    }

    private record Edge(int requiredKeys, int steps) {
    }

    private record RobotIndex(int index, char node) {
    }

    private record Robots(int robots, int size) {
        static Robots create(char[] robots) {
            assert robots.length <= 4;  // sizeof(int)
            int packed = 0;
            for (int i = 0; i < robots.length; i++) {
                packed |= (robots[i] & 0xFF) << 8 * i;
            }
            return new Robots(packed, robots.length);
        }

        char get(final int index) {
            return (char) (robots >> 8 * index & 0xFF);
        }

        Robots replace(final int index, final char node) {
            int newRobots = robots();
            final int shift = 8 * index;
            newRobots &= ~(0xFF << shift);
            newRobots |= (node & 0xFF) << shift;
            return new Robots(newRobots, size());
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append('{');
            for (int i = 0; i < size(); i++) {
                sb.append(get(i)).append(",");
            }
            if (sb.length() != 0) {
                sb.setLength(sb.length() - 1);
            }
            sb.append('}');
            return sb.toString();
        }
    }

    private record RobotsState(Robots robots, int keys, int steps) {
    }
}
