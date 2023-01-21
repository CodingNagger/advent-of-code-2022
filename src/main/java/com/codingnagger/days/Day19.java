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
        }

        long qualityLevel() {
            var maxRequiredRobots = Stream.of(ORE, CLAY, OBSIDIAN)
                    .collect(Collectors.toMap(
                            resource -> resource,
                            resource -> robotCost.values().stream()
                                    .mapToLong(v -> v.getOrDefault(resource, 0L)).max().orElseThrow()));

            var maxDuration = 24L;

            var maxGeodes = 0L;
            var start = new State(
                    Map.of(
                            ORE, 1L,
                            CLAY, 0L,
                            OBSIDIAN, 0L,
                            GEODE, 0L
                    ),
                    Map.of(
                            ORE, 0L,
                            CLAY, 0L,
                            OBSIDIAN, 0L,
                            GEODE, 0L
                    ),
                    0L);

            var queue = new PriorityQueue<State>();
            queue.add(start);

            while (!queue.isEmpty()) {
                var current = queue.poll();

                maxGeodes = Math.max(current.geodesCount(), maxGeodes);

                var possibleStates = new ArrayList<State>();

                for (var resource : List.of(GEODE, OBSIDIAN, CLAY, ORE)) {
                    if (maxRequiredRobots.containsKey(resource) &&
                            maxRequiredRobots.get(resource) <= current.robotCount(resource)) {
                        continue;
                    }

                    current.maybeNextStateForBuildingRobotFor(this, resource).ifPresent(possibleStates::add);
                }

                if (possibleStates.isEmpty()) {
                    possibleStates.add(current.nextStateForWaiting());
                }

                var statesUpToMaxDuration = possibleStates.stream()
                        .filter(state -> state.minutes <= maxDuration).collect(Collectors.toList());

                queue.addAll(statesUpToMaxDuration);
            }

            return id * maxGeodes;
        }
    }

    static class State implements Comparable<State> {
        final Map<String, Long> robots;
        final Map<String, Long> resources;
        private final Long minutes;

        State(Map<String, Long> robots, Map<String, Long> resources, Long minutes) {
            this.robots = Collections.unmodifiableMap(robots);
            this.resources = Collections.unmodifiableMap(resources);
            this.minutes = minutes;

//            System.out.println(this);
        }

        private static String serialiseMap(Map<String, Long> map) {
            return String.format("%s:%d;%s:%d;%s:%d;%s:%d",
                    GEODE, map.getOrDefault(GEODE, 0L),
                    OBSIDIAN, map.getOrDefault(OBSIDIAN, 0L),
                    CLAY, map.getOrDefault(CLAY, 0L),
                    ORE, map.getOrDefault(ORE, 0L)
            );
        }

        public Long robotCount(String resource) {
            return robots.getOrDefault(resource, 0L);
        }

        @Override
        public int compareTo(State o) {
            return geodesCount().compareTo(o.geodesCount());
        }

        private Optional<Long> maybeMinBuildWaitIfBuildable(Blueprint blueprint, String resource) {
            var resourceRobotCost = blueprint.robotCost.get(resource);

            if (resourceRobotCost.keySet().stream().anyMatch(r -> !robots.containsKey(r) || robots.get(r) == 0L)) {
                return Optional.empty();
            }

            return Optional.of(resourceRobotCost.keySet().stream()
                    .mapToLong(r ->
                            (long) Math.max(
                                    0L,
                                    Math.ceil(
                                            (resourceRobotCost.get(r) - resources.get(r)) / robotCount(r).floatValue()
                                    )
                            )
                    )
                    .max()
                    .orElseThrow() + 1);
        }

        public Optional<State> maybeNextStateForBuildingRobotFor(Blueprint blueprint, String resource) {

            var maybeMinBuildWait = maybeMinBuildWaitIfBuildable(blueprint, resource);

            if (maybeMinBuildWait.isEmpty()) {
                return Optional.empty();
            }

            var minBuildWait = maybeMinBuildWait.get();

            var nextStateRobots = new HashMap<>(this.robots);
            nextStateRobots.put(resource, nextStateRobots.getOrDefault(resource, 0L) + 1);

            var postPurchaseResources = new HashMap<>(this.resources);
            blueprint.robotCost.get(resource).forEach(
                    (r, c) -> postPurchaseResources.put(r, postPurchaseResources.get(r) - c));

            var nextStateResources = new HashMap<>(postPurchaseResources);
            this.robots.forEach((robot, count) ->
                    nextStateResources.put(robot, nextStateResources.getOrDefault(robot, 0L) + count * minBuildWait));

            return Optional.of(new State(
                    nextStateRobots,
                    nextStateResources,
                    this.minutes + minBuildWait
            ));
        }

        public State nextStateForWaiting() {
            var nextStateResources = new HashMap<>(this.resources);
            this.robots.forEach((robot, count) ->
                    nextStateResources.put(robot, nextStateResources.getOrDefault(robot, 0L) + count));

            return new State(
                    this.robots,
                    nextStateResources,
                    this.minutes + 1
            );
        }

        public Long geodesCount() {
            return resources.getOrDefault(GEODE, 0L);
        }

        @Override
        public String toString() {
            return String.format("Minute %d - Robots %s - Resources %s", minutes, serialiseMap(robots), serialiseMap(resources));
        }
    }
}
