package com.codingnagger.days;

import java.util.*;
import java.util.stream.Collectors;

public class Day15 implements Day {
    private final boolean test;

    public Day15() {
        this(false);
    }

    public Day15(boolean test) {
        this.test = test;
    }

    @Override
    public String partOne(List<String> input) {
        return new SensorMap(input).alternateCountLocationsBeaconsCantBe(test ? 10 : 2000000) + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return new SensorMap(input).distressBeaconTuningFrenquency(test ? 20 : 4000000) + "";
    }

    static class LocationRange {
        private final long y;
        private final long xStart;
        private final long xEnd;

        LocationRange(long y, long xStart, long xEnd) {
            this.y = y;
            this.xStart = xStart;
            this.xEnd = xEnd;
        }

        LocationRange merge(Collection<LocationRange> locationRanges) {
            assert locationRanges.stream().allMatch(this::canMerge);

            return new LocationRange(y,
                    Math.min(xStart, locationRanges.stream().mapToLong(r -> r.xStart).min().getAsLong()),
                    Math.max(xEnd, locationRanges.stream().mapToLong(r -> r.xEnd).max().getAsLong()));
        }

        private boolean canMerge(LocationRange locationRange) {
            return containsEdge(locationRange) || locationRange.containsEdge(this);
        }

        private boolean canSplitWith(Location location) {
            return y == location.y && containsX(location.x);
        }

        List<LocationRange> split(Location location) {
            assert containsX(location.x);

            return List.of(new LocationRange(y, xStart, location.x - 1), new LocationRange(y, location.x + 1, xEnd));
        }

        private boolean contains(LocationRange locationRange) {
            return y == locationRange.y &&
                    (containsX(locationRange.xStart) && containsX(locationRange.xEnd));
        }

        private boolean containsEdge(LocationRange locationRange) {
            return y == locationRange.y &&
                    (containsX(locationRange.xStart) || containsX(locationRange.xEnd));
        }

        boolean containsX(long x) {
            return xStart <= x && xEnd >= x;
        }

        long size() {
            return xEnd - xStart + 1;
        }

        @Override
        public String toString() {
            return "LocationRange{" +
                    "y=" + y +
                    ", xStart=" + xStart +
                    ", xEnd=" + xEnd +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocationRange that = (LocationRange) o;
            return y == that.y && xStart == that.xStart && xEnd == that.xEnd;
        }

        @Override
        public int hashCode() {
            return Objects.hash(y, xStart, xEnd);
        }

        public boolean canSubstract(LocationRange locationRange) {
            return !locationRange.contains(this) && y == locationRange.y &&
                    (containsX(locationRange.xStart) || containsX(locationRange.xEnd));
        }

        public List<LocationRange> substract(LocationRange locationRange) {
            if (contains(locationRange)) {
                return List.of(
                        new LocationRange(y, xStart, locationRange.xStart - 1),
                        new LocationRange(y, locationRange.xEnd + 1, xEnd)
                );
            } else if (containsX(locationRange.xStart)) {
                return List.of(
                        new LocationRange(y, xStart, locationRange.xStart - 1)
                );
            } else if (containsX(locationRange.xEnd)) {
                return List.of(
                        new LocationRange(y, locationRange.xEnd + 1, xEnd)
                );
            }

            return List.of(this);
        }
    }

    static class SensorMap {
        Set<Sensor> sensors;

        public SensorMap(List<String> input) {
            sensors = input.stream().map(Sensor::parse).collect(Collectors.toSet());
        }

        public long alternateCountLocationsBeaconsCantBe(long row) {
            return locationRangesBeaconCantBe(row, false).stream().mapToLong(LocationRange::size).sum();
        }

        Set<LocationRange> locationRangesBeaconCanBe(long minBound, long maxBound, long row) {
            var impossibleLocationRanges = locationRangesBeaconCantBe(row, true);

            var searchableRanges = Set.of(new LocationRange(row, minBound, maxBound));
            HashSet<LocationRange> substractedRanges;

            var splitSomething = true;

            while (splitSomething) {
                substractedRanges = new HashSet<>();
                splitSomething = false;

                for (var range : searchableRanges) {
                    var impossibleRange = impossibleLocationRanges.stream()
                            .filter(range::canSubstract)
                            .findFirst();

                    if (impossibleRange.isPresent()) {
                        substractedRanges.addAll(range.substract(impossibleRange.get()));
                        splitSomething = true;
                    } else {
                        substractedRanges.add(range);
                    }
                }

                searchableRanges = substractedRanges;
            }

            return searchableRanges;
        }

        Set<LocationRange> locationRangesBeaconCantBe(long row, boolean includeBeacons) {
            var ranges = new HashSet<LocationRange>();

            for (var sensor : sensors) {
                final var maybeLocationRange = sensor.maybeLocationRange(row);

                if (maybeLocationRange.isEmpty()) {
                    continue;
                }

                final var locationRange = maybeLocationRange.get();

                var candidateContainer = ranges.stream().filter(r -> r.contains(locationRange)).findFirst();

                if (candidateContainer.isPresent()) {
                    continue;
                }

                var candidateContent = ranges.stream().filter(locationRange::contains).findFirst();
                candidateContent.ifPresent(ranges::remove);

                ranges.add(locationRange);
            }

            var mergedRanges = ranges;
            HashSet<LocationRange> newRanges;

            var mergedSomething = true;

            while (mergedSomething) {
                newRanges = new HashSet<>();
                mergedSomething = false;

                for (var range : mergedRanges) {
                    var mergeableRanges = mergedRanges.stream()
                            .filter(range::canMerge)
                            .filter(r -> !r.equals(range))
                            .collect(Collectors.toUnmodifiableList());

                    if (mergeableRanges.isEmpty()) {
                        newRanges.add(range);
                    } else {
                        newRanges.add(range.merge(mergeableRanges));
                        mergedSomething = true;
                    }
                }

                mergedRanges = newRanges;
            }

            if (includeBeacons) {
                return mergedRanges;
            }

            var beacons = sensors.stream().map(s -> s.closestBeacon).distinct().collect(Collectors.toUnmodifiableList());

            var splitRanges = mergedRanges;

            HashSet<LocationRange> newSplitRanges;

            var splitSomething = true;

            while (splitSomething) {
                newSplitRanges = new HashSet<>();
                splitSomething = false;

                for (var range : splitRanges) {
                    var splitter = beacons.stream()
                            .filter(range::canSplitWith)
                            .findFirst();

                    if (splitter.isPresent()) {
                        newSplitRanges.addAll(range.split(splitter.get()));
                        splitSomething = true;
                    } else {
                        newSplitRanges.add(range);
                    }
                }

                splitRanges = newSplitRanges;
            }

            return splitRanges;
        }

        private SensorMap prettyPrint() {
            var beaconLocations = sensors.stream().map(s -> s.closestBeacon).collect(Collectors.toSet());

            var minSensorX = sensors.stream().mapToLong(s -> s.x).min().getAsLong();
            var minBeaconX = sensors.stream().mapToLong(s -> s.closestBeacon.x).min().getAsLong();
            var maxSensorX = sensors.stream().mapToLong(s -> s.x).max().getAsLong();
            var maxBeaconX = sensors.stream().mapToLong(s -> s.closestBeacon.x).max().getAsLong();

            var minX = Math.min(minSensorX, minBeaconX);
            var maxX = Math.max(maxSensorX, maxBeaconX);

            var minSensorY = sensors.stream().mapToLong(s -> s.y).min().getAsLong();
            var minBeaconY = sensors.stream().mapToLong(s -> s.closestBeacon.y).min().getAsLong();
            var maxSensorY = sensors.stream().mapToLong(s -> s.y).max().getAsLong();
            var maxBeaconY = sensors.stream().mapToLong(s -> s.closestBeacon.y).max().getAsLong();

            var minY = Math.min(minSensorY, minBeaconY);
            var maxY = Math.max(maxSensorY, maxBeaconY);

            var sb = new StringBuilder();

            for (var y = minY; y <= maxY; y++) {
                for (var x = minX; x <= maxX; x++) {
                    char c;

                    final var xx = x;
                    final var yy = y;
                    if (beaconLocations.stream().anyMatch(b -> b.x == xx && b.y == yy)) {
                        c = 'B';
                    } else if (sensors.stream().anyMatch(s -> s.x == xx && s.y == yy)) {
                        c = 'S';
                    } else if (sensors.stream().anyMatch(s -> s.manhattanDistance(new Location(xx, yy)) <= s.closestBeaconManhattanDistance())) {
                        c = '#';
                    } else {
                        c = '.';
                    }

                    sb.append(c);
                }
                sb.append('\n');
            }

            System.out.println(sb);
            System.out.println();
            System.out.println();

            return this;
        }

        public long distressBeaconTuningFrenquency(long maxBound) {

            var possibleLocations = new HashMap<Location, Integer>();

            for (var sensor : sensors) {
                var edgeDistance = sensor.closestBeaconManhattanDistance() + 1;

                for (var x = -edgeDistance; x <= edgeDistance; x++) {
                    var gap = edgeDistance - Math.abs(x);

                    final var plusGapLocation = new Location(sensor.x + x, sensor.y + gap);

                    if (plusGapLocation.x <= maxBound && plusGapLocation.y <= maxBound
                            && plusGapLocation.x >= 0 && plusGapLocation.y >= 0) {
//                        System.out.printf("plus check: %s%n", plusGapLocation);

                        if (sensors.stream().allMatch(s -> s.manhattanDistance(plusGapLocation) > s.closestBeaconManhattanDistance())) {
                            possibleLocations.put(plusGapLocation, possibleLocations.getOrDefault(plusGapLocation, 0) + 1);
                        }
                    }

                    final var minusGapLocation = new Location(sensor.x + x, sensor.y - gap);

                    if (minusGapLocation.x <= maxBound && minusGapLocation.y <= maxBound
                            && minusGapLocation.x >= 0 && minusGapLocation.y >= 0) {
//                        System.out.printf("minus check: %s%n", minusGapLocation);

                        if (sensors.stream().allMatch(s -> s.manhattanDistance(minusGapLocation) > s.closestBeaconManhattanDistance() + 1)) {
                            possibleLocations.put(minusGapLocation, possibleLocations.getOrDefault(minusGapLocation, 0) + 1);
                        }
                    }
                }
            }

            var highestCount = possibleLocations.values().stream().mapToInt(v -> v).max().getAsInt();
            var bestLocations = possibleLocations.entrySet().stream().filter(e -> e.getValue().equals(highestCount))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toUnmodifiableList());

            return 4000000 * bestLocations.get(0).x + bestLocations.get(0).y;
        }
    }

    static class Sensor extends Location {
        final Beacon closestBeacon;

        Sensor(long x, long y, Beacon closestBeacon) {
            super(x, y);
            this.closestBeacon = closestBeacon;
        }

        static Sensor parse(String line) {
            var sensorAndBeaconDetails = line.split(": ");

            var beaconDetails = sensorAndBeaconDetails[1].substring("closest beacon is at ".length()).split(", ");

            var beacon = new Beacon(
                    Long.parseLong(beaconDetails[0].substring("x=".length())),
                    Long.parseLong(beaconDetails[1].substring("y=".length()))
            );

            var sensorDetails = sensorAndBeaconDetails[0].substring("Sensor at ".length()).split(", ");

            return new Sensor(
                    Long.parseLong(sensorDetails[0].substring("x=".length())),
                    Long.parseLong(sensorDetails[1].substring("y=".length())),
                    beacon
            );
        }

        long closestBeaconManhattanDistance() {
            return manhattanDistance(closestBeacon);
        }

        @Override
        public String toString() {
            return super.toString() + " - closest beacon at " + closestBeacon.toString();
        }

        public Optional<LocationRange> maybeLocationRange(long row) {
            var rangeDepth = closestBeaconManhattanDistance();

            if (row > y + rangeDepth || row < y - rangeDepth) {
                return Optional.empty();
            }

            var gap = Math.abs(row - y);
            var xGap = Math.abs(rangeDepth - gap);

            return Optional.of(new LocationRange(row, x - xGap, x + xGap));
        }
    }

    static class Location {
        long x, y;

        Location(long x, long y) {
            this.x = x;
            this.y = y;
        }

        static Location origin() {
            return new Location(0, 0);
        }

        double distance(Location location) {
            return Math.sqrt(Math.pow(x - location.x, 2) + Math.pow(y - location.y, 2));
        }

        long manhattanDistance(Location location) {
            return Math.abs(x - location.x) + Math.abs(y - location.y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Location location = (Location) o;
            return x == location.x && y == location.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return String.format("(%d ; %d) ", x, y);
        }
    }

    static class Beacon extends Location {
        Beacon(long x, long y) {
            super(x, y);
        }
    }
}
