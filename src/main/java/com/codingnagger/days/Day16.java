package com.codingnagger.days;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day16 implements Day {
    @Override
    public String partOne(List<String> input) {
        return "" + new Volcano(input).maxPressure(30L);
    }

    @Override
    public String partTwo(List<String> input) {
        return "" + new Volcano(input).maxPressureTeamUp(26L);
    }

    static class Volcano {
        Map<String, SortedSet<String>> nodes;
        Map<String, Long> flowRates;
        Map<String, Long> cache;

        Volcano(List<String> input) {
            var nodes = new HashMap<String, SortedSet<String>>();
            var flowRates = new HashMap<String, Long>();

            for (var line : input) {
                var valve = extractValve(line);
                var flowRate = extractFlowRate(line);
                var connectedValves = extractConnectedValves(line);

                nodes.put(valve, new TreeSet<>(connectedValves));
                flowRates.put(valve, flowRate);
            }

            this.nodes = Collections.unmodifiableMap(nodes);
            this.flowRates = Collections.unmodifiableMap(flowRates);
            cache = new HashMap<>();
        }

        private static String extractValve(String line) {
            return line.split(" ")[1];
        }

        private List<String> extractConnectedValves(String line) {
            return Arrays.stream(line.split("; tunnels? leads? to valves? ")[1].split(", ")).collect(Collectors.toUnmodifiableList());
        }

        private long extractFlowRate(String line) {
            return Long.parseLong(line.split("; ")[0].substring("Valve AA has flow rate=".length()));
        }

        public long maxPressure(long totalDuration) {
            // max pressure at for point-time
            var start = "AA-1";

            var maxPressure = new HashMap<String, Long>();
            var maxOpenedValves = new HashMap<String, List<String>>();

            var stack = new Stack<String>();
            stack.push(start);

            while (!stack.isEmpty()) {
                var current = stack.pop();
                var splitCurrent = current.split("-");

                var position = splitCurrent[0];
                var time = Integer.parseInt(splitCurrent[1]);
                var nextTime = time + 1;

                if (time == totalDuration) continue;

                var openedValves = maxOpenedValves.getOrDefault(current, List.of());
                var nextPressure = maxPressure.getOrDefault(current, 0L) +
                        openedValves.stream().mapToLong(flowRates::get).sum();

                for (var candidatePosition : nodes.get(position)) {
                    var candidate = candidatePosition + "-" + nextTime;

                    var bestCandidatePositionPressure = maxPressure.getOrDefault(candidate, 0L);

                    if (!maxPressure.containsKey(candidate) || nextPressure > bestCandidatePositionPressure) {
                        maxPressure.put(candidate, nextPressure);
                        maxOpenedValves.put(candidate, openedValves);
                        stack.push(candidate);
                    }
                }

                if (!openedValves.contains(position)) {
                    var openCandidate = position + "-" + nextTime;

                    var bestOpenCandidatePositionPressure = maxPressure.getOrDefault(openCandidate, 0L);
                    var openingPressure = nextPressure + flowRates.get(position);

                    if (!maxPressure.containsKey(openCandidate) || openingPressure > bestOpenCandidatePositionPressure) {
                        maxPressure.put(openCandidate, openingPressure);
                        maxOpenedValves.put(openCandidate,
                                Stream.concat(Stream.of(position), openedValves.stream()).collect(Collectors.toList()));
                        stack.push(openCandidate);
                    }
                }
            }

            return maxPressure.entrySet().stream().filter(e -> Long.parseLong(e.getKey().split("-")[1]) == totalDuration).mapToLong(Map.Entry::getValue).max().getAsLong();
        }

        public long maxPressureTeamUp(long totalDuration) {
            // max pressure at for point-time
            var start = "AA;AA-1";

            var maxPressure = new HashMap<String, Long>();
            var maxOpenedValves = new HashMap<String, List<String>>();
            var openableValves = flowRates.entrySet().stream().filter(e -> e.getValue() > 0L).map(Map.Entry::getKey).collect(Collectors.toSet());

            var stack = new Stack<String>();
            stack.push(start);

            while (!stack.isEmpty()) {
                var current = stack.pop();
                var splitCurrent = current.split("-");

                var splitPosition = splitCurrent[0].split(";");
                var humanPosition = splitPosition[0];
                var elephantPosition = splitPosition[1];
                var time = Integer.parseInt(splitCurrent[1]);
                var nextTime = time + 1;

                if (time == totalDuration) continue;

                var openedValves = maxOpenedValves.getOrDefault(current, List.of());
                var nextPressure = maxPressure.getOrDefault(current, 0L) +
                        openedValves.stream().mapToLong(flowRates::get).sum();

//                System.out.printf("Time: %d - Pressure at %d - Human at %s - Elephant at %s - next pressure at %d - opened: %s%n",
//                        time, openedValves.stream().mapToLong(flowRates::get).sum(),
//                        humanPosition, elephantPosition, nextPressure, String.join(";", openedValves));

                for (var candidatePositionForHuman : nodes.get(humanPosition)) {
                    for (var candidatePositionForElephant : nodes.get(elephantPosition)) {
                        if (candidatePositionForHuman.equals(candidatePositionForElephant)) {
                            continue;
                        }

                        var candidate = candidatePositionForHuman + ";" + candidatePositionForElephant + "-" + nextTime;

                        var bestCandidatePositionPressure = maxPressure.getOrDefault(candidate, 0L);

                        if (!maxPressure.containsKey(candidate) || nextPressure > bestCandidatePositionPressure) {
                            maxPressure.put(candidate, nextPressure);
                            maxOpenedValves.put(candidate, openedValves);
                            stack.push(candidate);
                        }
                    }
                }

                if (!openedValves.contains(humanPosition) && openableValves.contains(humanPosition) &&
                        !openedValves.contains(elephantPosition) && openableValves.contains(elephantPosition)) {
                    var openCandidate = humanPosition + ";" + elephantPosition + "-" + nextTime;

                    var bestOpenCandidatePositionPressure = maxPressure.getOrDefault(openCandidate, 0L);
                    var openingPressure = nextPressure + flowRates.get(humanPosition) + flowRates.get(elephantPosition);

                    if (!maxPressure.containsKey(openCandidate) || openingPressure > bestOpenCandidatePositionPressure) {
                        maxPressure.put(openCandidate, openingPressure);
                        maxOpenedValves.put(openCandidate,
                                Stream.concat(Stream.of(humanPosition, elephantPosition), openedValves.stream()).collect(Collectors.toList()));
                        stack.push(openCandidate);
                    }
                }

                if (!openedValves.contains(humanPosition) && openableValves.contains(humanPosition)) {
                    var openingPressure = nextPressure + flowRates.get(humanPosition);

                    for (var candidatePositionForElephant : nodes.get(elephantPosition)) {
                        if (humanPosition.equals(candidatePositionForElephant)) {
                            continue;
                        }

                        var candidate = humanPosition + ";" + candidatePositionForElephant + "-" + nextTime;

                        var bestCandidatePositionPressure = maxPressure.getOrDefault(candidate, 0L);

                        if (!maxPressure.containsKey(candidate) || openingPressure > bestCandidatePositionPressure) {
                            maxPressure.put(candidate, openingPressure);
                            maxOpenedValves.put(candidate,
                                    Stream.concat(Stream.of(humanPosition), openedValves.stream()).collect(Collectors.toList()));
                            stack.push(candidate);
                        }
                    }
                }

                if (!openedValves.contains(elephantPosition) && openableValves.contains(elephantPosition)) {
                    var openingPressure = nextPressure + flowRates.get(elephantPosition);

                    for (var candidatePositionForHuman : nodes.get(humanPosition)) {
                        if (elephantPosition.equals(candidatePositionForHuman)) {
                            continue;
                        }

                        var candidate = candidatePositionForHuman + ";" + elephantPosition + "-" + nextTime;

                        var bestCandidatePositionPressure = maxPressure.getOrDefault(candidate, 0L);

                        if (!maxPressure.containsKey(candidate) || openingPressure > bestCandidatePositionPressure) {
                            maxPressure.put(candidate, openingPressure);
                            maxOpenedValves.put(candidate,
                                    Stream.concat(Stream.of(elephantPosition), openedValves.stream()).collect(Collectors.toList()));
                            stack.push(candidate);
                        }
                    }
                }
            }

            return maxPressure.entrySet().stream().filter(e -> Long.parseLong(e.getKey().split("-")[1]) == totalDuration).mapToLong(Map.Entry::getValue).max().getAsLong();
        }
    }
}
