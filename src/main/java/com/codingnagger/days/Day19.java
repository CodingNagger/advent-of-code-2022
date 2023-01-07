package com.codingnagger.days;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day19 implements Day {
    @Override
    public String partOne(List<String> input) {
        return input.stream().map(Blueprint::new).mapToLong(Blueprint::qualityLevel).sum() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return null;
    }

    static class Blueprint {
        public static final String GEODE = "geode";
        public static final String OBSIDIAN = "obsidian";
        public static final String CLAY = "clay";
        public static final String ORE = "ore";
        private final long id;
        private final Map<String, Map<String, Long>> robotCost;

        Blueprint(String description) {
            var idAndRobots = description.split(": ");
            this.id = Long.parseLong(idAndRobots[0].substring("Blueprint ".length()));
            this.robotCost = new HashMap<>();

            Pattern pattern = Pattern.compile("Each ([a-z]+) robot costs (\\d+) ([a-z]+)( and (\\d+) ([a-z]+))?");
            Matcher matcher = pattern.matcher(idAndRobots[1]);

            while (matcher.find()) {
                var costs = new HashMap<String, Long>();

                costs.put(matcher.group(3), Long.parseLong(matcher.group(2)));
                if (matcher.group(4) != null) {
                    costs.put(matcher.group(6), Long.parseLong(matcher.group(5)));
                }

                this.robotCost.put(matcher.group(1), costs);
            }

            // number of obsidian rounds to get one geode
            // number of clay rounds to get one obsidian
            // number of ore to get clay

            // figure how many machines we need
            // count how many obsidian machine we need to get one geode
            // count how many clay machines we need to get one obsidian machine
            // count how many ore machines we need to get one clay machine
        }

        public static Map<String, Long> parseMap(String value) {
            return Arrays.stream(value.split(";"))
                    .map(s -> s.split(":"))
                    .collect(Collectors.toMap(s -> s[0], s -> Long.parseLong(s[1])));
        }

        public static String serialiseMap(Map<String, Long> map) {
            return String.format("%s:%d;%s:%d;%s:%d;%s:%d",
                    GEODE, map.getOrDefault(GEODE, 0L),
                    OBSIDIAN, map.getOrDefault(OBSIDIAN, 0L),
                    CLAY, map.getOrDefault(CLAY, 0L),
                    ORE, map.getOrDefault(ORE, 0L)
            );
        }

        public static boolean newerIsBetter(Map<String, Long> newer, Map<String, Long> older) {
            if (newer.getOrDefault(GEODE, 0L) > older.getOrDefault(GEODE, 0L)) {
                return true;
            }
            if (newer.getOrDefault(OBSIDIAN, 0L) > older.getOrDefault(OBSIDIAN, 0L)) {
                return true;
            }
            if (newer.getOrDefault(CLAY, 0L) > older.getOrDefault(CLAY, 0L)) {
                return true;
            }
            return newer.getOrDefault(ORE, 0L) > older.getOrDefault(ORE, 0L);
        }

        long qualityLevel() {
            final var totalDuration = 24;
            var start = createState(Map.of(ORE, 1L), 1);
            var maxStartResources = new HashMap<String, Map<String, Long>>();
            var maxEndResources = new HashMap<String, Map<String, Long>>();

            var visited = new ArrayList<String>();
            var stack = new Stack<String>();
            stack.push(start);

            while (!stack.isEmpty()) {
                var current = stack.pop();

                if (visited.contains(current)) {
                    continue;
                }

                visited.add(current);

                var splitCurrent = current.split("\\|");

                var robots = parseMap(splitCurrent[0]);
                var resources = maxStartResources.getOrDefault(current, Map.of());
                var time = Integer.parseInt(splitCurrent[1]);
                var nextTime = time + 1;

                // minute 1 starts with 0 ore and 1 ore robot
                // minute 1 ends with 1 ore and 1 ore robot

                // minute 2 starts with 1 ore and 1 ore robot
                // minute 2 ends with 2 ore and 1 ore robot

                // minute 3 starts with 2 ore and 1 ore robot
                // minute 3 ends with 2-2+1=1 ore and 1 clay and 1 ore robot

                // minute 4 starts with 1 ore and 1 clay and 1 ore robot
                // minute 4 ends with 2 ore and 1 clay and 1 ore and clay robot

                // minute 5 starts with 2 ore and 1 clay and 1 ore and clay robot
                // minute 5 ends with 2-2+1=1 ore and 2 clay and 1 ore and clay robot

                System.out.printf("Minute %d start - Robots %s - Resources %s%n", time, serialiseMap(robots), serialiseMap(resources));

                var updatedResources = new HashMap<String, Long>();
                robots.forEach((resource, count) ->
                        updatedResources.put(resource, resources.getOrDefault(resource, 0L) + count));

                if (!maxEndResources.containsKey(current) ||
                        newerIsBetter(updatedResources, maxEndResources.get(current))) {
                    System.out.printf("Minute %d end - Robots %s - Resources %s - no build%n", time, serialiseMap(robots), serialiseMap(updatedResources));
                    maxEndResources.put(current, Collections.unmodifiableMap(updatedResources));
                }

                if (time == totalDuration) continue;

                var nextState = createState(robots, nextTime);
                if (!maxStartResources.containsKey(nextState) ||
                        newerIsBetter(updatedResources, maxStartResources.get(nextState))) {
                    System.out.printf("Minute %d end - Robots %s - Resources %s - no build%n", time, serialiseMap(robots), serialiseMap(updatedResources));
                    maxStartResources.put(nextState, Collections.unmodifiableMap(updatedResources));
                    stack.push(nextState);
                    visited.remove(nextState);
                }

                if (canAffordRobot(GEODE, resources)) {
                    buildRobot(robots, GEODE, updatedResources, nextTime, maxStartResources, time, stack, visited);
                } else if (canAffordRobot(OBSIDIAN, resources)) {
                    buildRobot(robots, OBSIDIAN, updatedResources, nextTime, maxStartResources, time, stack, visited);
                } else if (canAffordRobot(CLAY, resources)) {
                    buildRobot(robots, CLAY, updatedResources, nextTime, maxStartResources, time, stack, visited);
                } else if (canAffordRobot(ORE, resources)) {
                    buildRobot(robots, ORE, updatedResources, nextTime, maxStartResources, time, stack, visited);
                }
            }

            return id * maxEndResources.entrySet().stream()
                    .filter(e -> e.getKey().endsWith("|" + totalDuration))
                    .mapToLong(e -> e.getValue().getOrDefault(GEODE, 0L))
                    .max()
                    .orElseThrow();
        }

        private void buildRobot(Map<String, Long> robots, String ore, HashMap<String, Long> updatedResources, int nextTime, HashMap<String, Map<String, Long>> maxResources, int time, Stack<String> stack, ArrayList<String> visited) {
            var candidateRobots = new HashMap<>(robots);
            candidateRobots.put(ore, candidateRobots.getOrDefault(ore, 0L) + 1L);

            var candidateResources = new HashMap<>(updatedResources);
            for (var cost : robotCost.get(ore).entrySet()) {
                candidateResources.put(
                        cost.getKey(),
                        candidateResources.get(cost.getKey()) - cost.getValue()
                );
            }

            var candidateNextState = createState(candidateRobots, nextTime);

            if (!maxResources.containsKey(candidateNextState) ||
                    newerIsBetter(candidateResources, maxResources.get(candidateNextState))) {
                System.out.printf("Minute %d end - Robots %s - Resources %s - building %s robot%n", time, serialiseMap(candidateRobots), serialiseMap(candidateResources), ore);

                maxResources.put(candidateNextState, Collections.unmodifiableMap(candidateResources));
                stack.push(candidateNextState);
                visited.remove(candidateNextState);
            } else {
                System.out.printf("Minute %d end - Robots %s - Resources %s - building %s robot - skipped%n", time, serialiseMap(candidateRobots), serialiseMap(candidateResources), ore);

            }
        }

        private String createState(Map<String, Long> robots, int nextTime) {
            return serialiseMap(robots) + "|" + nextTime;
        }

        private boolean canAffordRobot(String robot, Map<String, Long> resources) {
            var canAfford = true;

            for (var cost : robotCost.get(robot).entrySet()) {
                canAfford &= resources.containsKey(cost.getKey()) && resources.get(cost.getKey()) >= cost.getValue();
            }

            return canAfford;
        }
    }
}
