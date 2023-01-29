package com.codingnagger.days;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codingnagger.days.Day19.Blueprint.*;

public class Day19 implements Day {
    @Override
    public String partOne(List<String> input) {
        return input.stream().map(Blueprint::new).mapToInt(Blueprint::qualityLevel).sum() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return "" + (
                new Blueprint(input.get(0)).maxGeodes(32)
                        * new Blueprint(input.get(1)).maxGeodes(32)
                        * (input.size() > 2 ? new Blueprint(input.get(2)).maxGeodes(32) : 1)
        );
    }

    static class Blueprint {
        public static final String GEODE = "geode";
        public static final List<String> GEODES_ONLY = List.of(GEODE);
        public static final String OBSIDIAN = "obsidian";
        public static final String CLAY = "clay";
        public static final String ORE = "ore";
        public static final List<String> ALL = List.of(GEODE, OBSIDIAN, CLAY, ORE);
        public static final List<String> ONLY_USEFUL_FOR_LAST_ROUND_GEODES = List.of(GEODE, OBSIDIAN, ORE);
        private final int id;
        private final Map<String, Map<String, Integer>> robotCost;

        Blueprint(String description) {
            var idAndRobots = description.split(": ");
            this.id = Integer.parseInt(idAndRobots[0].substring("Blueprint ".length()));
            this.robotCost = new HashMap<>();

            Pattern pattern = Pattern.compile("Each ([a-z]+) robot costs (\\d+) ([a-z]+)( and (\\d+) ([a-z]+))?");
            Matcher matcher = pattern.matcher(idAndRobots[1]);

            while (matcher.find()) {
                var costs = new HashMap<String, Integer>();

                costs.put(matcher.group(3), Integer.parseInt(matcher.group(2)));
                if (matcher.group(4) != null) {
                    costs.put(matcher.group(6), Integer.parseInt(matcher.group(5)));
                }

                this.robotCost.put(matcher.group(1), costs);
            }
        }

        private static List<String> getRobotCandidatesForMinutesAndMaxDuration(Integer minutes, int maxDuration) {
            if (minutes == maxDuration - 1) {
                return GEODES_ONLY;
            }
            if (minutes == maxDuration - 2) {
                return ONLY_USEFUL_FOR_LAST_ROUND_GEODES;
            }
            return ALL;
        }

        int qualityLevel() {
            return id * maxGeodes(24);
        }

        int maxGeodes(int maxDuration) {
            var maxRequiredRobots = Stream.of(ORE, CLAY, OBSIDIAN)
                    .collect(Collectors.toMap(
                            resource -> resource,
                            resource -> robotCost.values().stream()
                                    .mapToInt(v -> v.getOrDefault(resource, 0)).max().orElseThrow()));

            var maxGeodes = 0;

            var start = new State(
                    Map.of(
                            ORE, 1,
                            CLAY, 0,
                            OBSIDIAN, 0,
                            GEODE, 0
                    ),
                    Map.of(
                            ORE, 0,
                            CLAY, 0,
                            OBSIDIAN, 0,
                            GEODE, 0
                    ),
                    0
            );

            var queue = new PriorityQueue<State>();
            queue.add(start);

            while (!queue.isEmpty()) {
                var current = queue.poll();

                var currentGeodePotential = current.potentialGeodesAt(maxDuration);

                if (maxGeodes > 0 && currentGeodePotential < maxGeodes) {
                    continue;
                }

                var currentGeodesCount = current.geodesCount();

                maxGeodes = Math.max(currentGeodesCount, maxGeodes);

                if (maxDuration == current.minutes) {
                    continue;
                }

                var possibleStates = new ArrayList<State>();

                for (var resource : getRobotCandidatesForMinutesAndMaxDuration(current.minutes, maxDuration)) {
                    if (maxRequiredRobots.containsKey(resource) &&
                            maxRequiredRobots.get(resource) <= current.robotCount(resource)) {
                        continue;
                    }

                    current.maybeNextStateForBuildingRobotFor(this, resource, maxDuration).ifPresent(possibleStates::add);
                }

                if (possibleStates.isEmpty()) {
                    possibleStates.add(current.nextStateForWaiting());
                }

                queue.addAll(possibleStates);
            }

            System.out.printf("Quality level for blueprint %d with %d geodes open = %d%n", id, maxGeodes, id * maxGeodes);
            return maxGeodes;
        }
    }

    static class State implements Comparable<State> {
        final Map<String, Integer> robots;
        final Map<String, Integer> resources;
        private final Integer minutes;

        State(Map<String, Integer> robots, Map<String, Integer> resources, Integer minutes) {
            this.robots = robots;
            this.resources = resources;
            this.minutes = minutes;
        }

        private static String serialiseMap(Map<String, Integer> map) {
            return String.format("%s:%d;%s:%d;%s:%d;%s:%d",
                    GEODE, map.get(GEODE),
                    OBSIDIAN, map.get(OBSIDIAN),
                    CLAY, map.get(CLAY),
                    ORE, map.get(ORE)
            );
        }

        public Integer robotCount(String resource) {
            return robots.get(resource);
        }

        @Override
        public int compareTo(State o) {
//            return geodesCount() - o.geodesCount();
            return minutes - o.minutes;
        }

        private Optional<Integer> maybeMinBuildWaitIfBuildable(Blueprint blueprint, String resource) {
            var resourceRobotCost = blueprint.robotCost.get(resource);

            if (resourceRobotCost.keySet().stream().anyMatch(r -> !robots.containsKey(r) || robots.get(r) == 0)) {
                return Optional.empty();
            }

            return Optional.of(resourceRobotCost.keySet().stream()
                    .mapToInt(r ->
                            (int) Math.max(
                                    0,
                                    Math.ceil(
                                            (resourceRobotCost.get(r) - resources.get(r)) / robotCount(r).floatValue()
                                    )
                            )
                    )
                    .max()
                    .orElseThrow() + 1);
        }

        public Optional<State> maybeNextStateForBuildingRobotFor(Blueprint blueprint, String resource, int maxDuration) {
            var maybeMinBuildWait = maybeMinBuildWaitIfBuildable(blueprint, resource);

            if (maybeMinBuildWait.isEmpty()) {
                return Optional.empty();
            }

            var minBuildWait = maybeMinBuildWait.get();
            var nextStateMinutes = this.minutes + minBuildWait;

            if (nextStateMinutes > maxDuration) {
                return Optional.empty();
            }

            var nextStateRobots = new HashMap<>(this.robots);
            nextStateRobots.put(resource, nextStateRobots.get(resource) + 1);

            var postPurchaseResources = new HashMap<>(this.resources);
            blueprint.robotCost.get(resource).forEach(
                    (r, c) -> postPurchaseResources.put(r, postPurchaseResources.get(r) - c));

            var nextStateResources = new HashMap<>(postPurchaseResources);
            this.robots.forEach((robot, count) ->
                    nextStateResources.put(robot, nextStateResources.get(robot) + count * minBuildWait));

            return Optional.of(new State(
                    nextStateRobots,
                    nextStateResources,
                    nextStateMinutes
            ));
        }

        public State nextStateForWaiting() {
            var nextStateResources = new HashMap<>(this.resources);
            this.robots.forEach((robot, count) ->
                    nextStateResources.put(robot, nextStateResources.get(robot) + count));

            return new State(
                    new HashMap<>(this.robots),
                    nextStateResources,
                    this.minutes + 1
            );
        }

        public int geodesCount() {
            return resources.get(GEODE);
        }

        @Override
        public String toString() {
            return String.format("Minute %d\t- Robots %s\t- Resources %s", minutes, serialiseMap(robots), serialiseMap(resources));
        }

        public int potentialGeodesAt(int maxDuration) {
            return resources.get(GEODE) + (maxDuration - minutes) * robots.get(GEODE);
        }
    }
}
