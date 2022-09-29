package com.shpikat.adventofcode2019;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.shpikat.adventofcode2019.Utils.readInput;

public class Day25 {

    static class Part1 {

        static int solve(final String input) throws InterruptedException, ExecutionException, IOException {
            final Intcode computer = Intcode.fromInput(input, 1024);

            final Droid droid = new Droid(computer);
            droid.init();

            final int password = droid.getMainAirlockPassword();

            droid.shutdown();

            return password;
        }
    }

    private static class Droid {

        private static final Pattern pattern = Pattern.compile("^\\s+== (?<name>.+) ==\\s+(?<description>[^\n]+)\\s+Doors here lead:\\s+(?<doors>(.+\n)+)\n(Items here:\\s+(?<items>(.+\n)+)\n)?(?<prompt>.+\n)$");
        private static final Long EOL = 10L;
        private static final String PROMPT = "Command?" + (char) EOL.intValue();
        private static final int PROMPT_LENGTH = PROMPT.length();

        private static final Map<String, String> returningDoors = Map.of(
                "north", "south",
                "east", "west",
                "south", "north",
                "west", "east"
        );

        private final ExecutorService executor = Executors.newSingleThreadExecutor();


        /* Pre-populated manually list to avoid writing the code that detects all possible disasters. */
        private final Collection<String> itemsToAvoid = new HashSet<>(Arrays.asList(
                "escape pod",
                "giant electromagnet",
                "photons",
                "infinite loop",
                "molten lava"
        ));

        private final Intcode computer;

        private Future<Void> future;

        private List<String> pathToSecurityCheckpoint = null;

        private String exitAtSecurityCheckpoint = null;

        private Droid(final Intcode computer) {
            this.computer = computer;
        }

        void init() {
            reset();
        }

        void shutdown() {
            if (future != null) {
                future.cancel(true);
            }
            executor.shutdown();
        }

        int getMainAirlockPassword() throws InterruptedException {
            /* Walk all the rooms, collecting all the items. */
            final List<String> items = new ArrayList<>();
            while (!explore(new HashSet<>(), new ArrayDeque<>(), items, null)) {
                /* When shouldn't have taken the item, start all over. */
                items.clear();
                reset();
            }

            return passSecurityCheckpoint(items);
        }

        private boolean explore(final Set<String> rooms, final Deque<String> path, final List<String> inventory, final String cameFrom) throws InterruptedException {
            final String output = read();
            final Matcher matcher = pattern.matcher(output);
            if (matcher.matches()) {
                if (matcher.group("prompt").equals(PROMPT)) {
                    final String name = matcher.group("name");
                    final boolean isSecurityCheckpoint = rooms.add(name) && name.equals("Security Checkpoint");

                    final String items = matcher.group("items");
                    if (items != null) {
                        for (final String line : items.split("\n")) {
                            final String item = line.substring(2);
                            if (!itemsToAvoid.contains(item)) {
                                write("take " + item);
                                final String result = read();
                                if (result.endsWith(PROMPT)) {
                                    inventory.add(item);
                                } else {
                                    System.out.println(result);
                                    itemsToAvoid.add(item);
                                    return false;
                                }
                            }
                        }
                    }

                    for (final String line : matcher.group("doors").split("\n")) {
                        final String door = line.substring(2);
                        if (!door.equals(cameFrom)) {
                            if (isSecurityCheckpoint) {
                                // There should be only two doors at the Checkpoint
                                this.pathToSecurityCheckpoint = new ArrayList<>(path);
                                this.exitAtSecurityCheckpoint = door;
                            } else {
                                path.addLast(door);
                                write(door);
                                if (!explore(rooms, path, inventory, returningDoors.get(door))) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (cameFrom != null) {
                        path.removeLast();
                        write(cameFrom);
                        read();
                    }
                } else {
                    System.err.println("No prompt: " + output);
                }
            } else {
                System.err.println("Cannot match: " + output);
            }

            return true;
        }

        private int passSecurityCheckpoint(final List<String> items) throws InterruptedException {
            // Get to the checkpoint
            for (final String door : pathToSecurityCheckpoint) {
                write(door);
                read();
            }

            // Start with an empty inventory
            for (final String item : items) {
                write("drop " + item);
                read();
            }

            // Use Gray codes to find the combination of items weights to get past the checkpoint
            int prev = 0;
            for (int i = 1; i < 1 << items.size(); i++) {
                final int current = i ^ (i >> 1);
                final int flippedBit = prev ^ current;
                final String command = (current & flippedBit) == 0 ? "drop " : "take ";
                write(command + items.get(Integer.numberOfTrailingZeros(flippedBit)));
                read();

                write(exitAtSecurityCheckpoint);
                final String output = read();
                if (!output.endsWith(PROMPT)) {
                    /*
                    Items in your inventory:
                    - planetoid
                    - sand
                    - pointer
                    - wreath
                     */
                    final Matcher matcher = Pattern.compile("(\\d+)").matcher(output);
                    if (matcher.find()) {
                        return Integer.parseInt(matcher.group(1));
                    } else {
                        System.err.println(output);
                        throw new IllegalStateException("No digital password detected");
                    }
                }
                prev = current;
            }

            throw new IllegalStateException("No suitable item combination found");
        }

        private void reset() {
            if (future != null) {
                future.cancel(true);
            }
            future = executor.submit(computer);
        }

        private String read() throws InterruptedException {
            final StringBuilder sb = new StringBuilder();
            while (sb.length() < PROMPT_LENGTH || !sb.substring(sb.length() - PROMPT_LENGTH).equals(PROMPT)) {
                final Long value = computer.output.poll(100, TimeUnit.MILLISECONDS);
                if (value == null) {
                    break;
                }
                sb.append((char) value.intValue());
            }
            return sb.toString();
        }

        private void write(final String line) throws InterruptedException {
            for (int i = 0; i < line.length(); i++) {
                computer.input.put((long) line.charAt(i));
            }
            computer.input.put(EOL);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final Intcode computer = Intcode.fromInput(readInput("day25_input.txt"), 1024);
        final Droid droid = new Droid(computer);

        droid.init();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                final String output = droid.read();
                if (output.isEmpty()) {
                    System.err.println("No data received.");
                    break;
                }
                System.out.println(output);
                final String input = br.readLine();
                if (input.isEmpty()) {
                    System.out.println("No command, ending session.");
                    break;
                }
                droid.write(input);
            }
        }

        droid.shutdown();
    }
}
