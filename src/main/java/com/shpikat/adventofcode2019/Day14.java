package com.shpikat.adventofcode2019;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14 {

    private static final Pattern pattern = Pattern.compile("(\\d+) ([A-Z]+)");

    static class Part1 {
        static int solve(final String input) {
            final Map<String, Reaction> reactions = readAllReactions(input);

            final Map<String, Integer> materialRequests = new HashMap<>();
            final Map<String, Integer> leftOvers = new HashMap<>();
            materialRequests.put("FUEL", 1);
            int amountOre = 0;
            while (!materialRequests.isEmpty()) {
                final Iterator<Map.Entry<String, Integer>> iterator = materialRequests.entrySet().iterator();
                final Map.Entry<String, Integer> request = iterator.next();
                iterator.remove();
                final String material = request.getKey();
                final int amountNeeded = request.getValue();
                final int currentLeftOver = leftOvers.getOrDefault(material, 0);
                if (amountNeeded <= currentLeftOver) {
                    leftOvers.put(material, currentLeftOver - amountNeeded);
                } else {
                    final int amountStillNeeded = amountNeeded - currentLeftOver;
                    final Reaction reaction = reactions.get(material);
                    final int nReactionsRequired = (int) Math.ceil((double) amountStillNeeded / reaction.outputAmount);
                    leftOvers.put(material, nReactionsRequired * reaction.outputAmount - amountStillNeeded);
                    for (final ReactionPart reactionPart : reaction.input) {
                        if (reactionPart.material.equals("ORE")) {
                            amountOre += nReactionsRequired * reactionPart.amount;
                        } else {
                            materialRequests.merge(reactionPart.material, nReactionsRequired * reactionPart.amount, Integer::sum);
                        }
                    }
                }
            }

            return amountOre;
        }
    }

    static class Part2 {
        private static final long AMOUNT_ORE = 1_000_000_000_000L;

        static long solve(final String input) {
            final Map<String, Reaction> reactions = readAllReactions(input);
            final long orePerFuel = Part1.solve(input);

            final Map<String, Long> materialRequests = new HashMap<>();
            final Map<String, Long> leftOvers = new HashMap<>();
            long amountOre = AMOUNT_ORE;
            long amountFuel = 0L;
            while (amountOre > 0) {
                if (materialRequests.isEmpty()) {
                    final long moreFuel = Math.max(amountOre / orePerFuel, 1);
                    materialRequests.put("FUEL", moreFuel);
                    amountFuel += moreFuel;
                }
                final Iterator<Map.Entry<String, Long>> iterator = materialRequests.entrySet().iterator();
                final Map.Entry<String, Long> request = iterator.next();
                iterator.remove();
                final String material = request.getKey();
                final long amountNeeded = request.getValue();
                final long currentLeftOver = leftOvers.getOrDefault(material, 0L);
                if (amountNeeded <= currentLeftOver) {
                    leftOvers.put(material, currentLeftOver - amountNeeded);
                } else {
                    final long amountStillNeeded = amountNeeded - currentLeftOver;
                    final Reaction reaction = reactions.get(material);
                    final long nReactionsRequired = (long) Math.ceil((double) amountStillNeeded / reaction.outputAmount);
                    leftOvers.put(material, nReactionsRequired * reaction.outputAmount - amountStillNeeded);
                    for (final ReactionPart reactionPart : reaction.input) {
                        if (reactionPart.material.equals("ORE")) {
                            amountOre -= nReactionsRequired * reactionPart.amount;
                        } else {
                            materialRequests.merge(reactionPart.material, nReactionsRequired * reactionPart.amount, Long::sum);
                        }
                    }
                }
            }

            return amountOre == 0 ? amountFuel : amountFuel - 1;
        }
    }

    private static Map<String, Reaction> readAllReactions(final String input) {
        final Map<String, Reaction> reactions = new HashMap<>();
        for (final String line : input.split("\n")) {
            final Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) {
                throw new IllegalStateException("No material in line " + line);
            }
            final Collection<ReactionPart> inputReactions = new ArrayList<>();
            ReactionPart part = getReactionPart(matcher);
            while (matcher.find(matcher.end())) {
                inputReactions.add(part);
                // the last one will be the output
                part = getReactionPart(matcher);
            }

            reactions.put(part.material, new Reaction(part.amount, inputReactions));
        }
        return reactions;
    }

    private static ReactionPart getReactionPart(final Matcher matcher) {
        final int amount = Integer.parseInt(matcher.group(1));
        final String material = matcher.group(2);
        return new ReactionPart(amount, material);
    }

    private record Reaction(int outputAmount, Collection<ReactionPart> input) {
    }

    private record ReactionPart(int amount, String material) {

        @Override
        public String toString() {
            return amount + " " + material;
        }
    }
}
