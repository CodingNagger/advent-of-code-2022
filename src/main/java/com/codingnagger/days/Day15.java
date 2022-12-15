package com.codingnagger.days;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

        LocationRange merge(LocationRange locationRange) {
            assert canMerge(locationRange);

            return new LocationRange(y, Math.min(xStart, locationRange.xStart), Math.max(xEnd, locationRange.xEnd));
        }

        private boolean canMerge(LocationRange locationRange) {
            return containsEdge(locationRange) || locationRange.containsEdge(this);
        }

        List<LocationRange> split(long x) {
            assert containsX(x);

            return List.of(new LocationRange(y, xStart, x - 1), new LocationRange(y, x + 1, xEnd));
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
    }

    static class SensorMap {
        Set<Sensor> sensors;

        public SensorMap(List<String> input) {
            sensors = input.stream().map(Sensor::parse).collect(Collectors.toSet());
        }

        public int countLocationsBeaconsCantBe(long row) {
            return locationsBeaconCantBe(row).size();
        }

        public long alternateCountLocationsBeaconsCantBe(long row) {
            return locationRangesBeaconCantBe(row).stream().mapToLong(LocationRange::size).sum();
        }

        Set<Location> locationsBeaconCantBe(long row) {
            var result = new HashSet<Location>();

            for (var sensor : sensors) {
                var minCandidateX = sensor.x - sensor.closestBeaconManhattanDistance();
                var maxCandidateX = sensor.x + sensor.closestBeaconManhattanDistance();

                for (var x = minCandidateX; x <= maxCandidateX; x++) {
                    if (sensor.y == row && sensor.x == x) {
                        continue;
                    }

                    if (sensor.closestBeacon.y == row && sensor.closestBeacon.x == x) {
                        continue;
                    }

                    var location = new Location(x, row);

                    if (sensor.manhattanDistance(location) <= sensor.closestBeaconManhattanDistance()) {
                        result.add(location);
                    }
                }
            }

            return result;
        }

        Set<LocationRange> locationRangesBeaconCantBe(long row) {
            var result = new HashSet<LocationRange>();

            for (var sensor : sensors) {
                var candidateRange = new LocationRange(row, sensor.x - 1 - sensor.closestBeaconManhattanDistance(), sensor.x - 1 + sensor.closestBeaconManhattanDistance());

//                var candidateContainer = result.stream().filter(r -> r.contains(candidateRange)).findFirst();
//
//                if (candidateContainer.isPresent()) {
//                    continue;
//                }
//
//                var candidateContent = result.stream().filter(candidateRange::contains).findFirst();
//
//                if (candidateContent.isPresent()) {
//                    result.remove(candidateContent.get());
//                    result.add(candidateRange);
//                } else {
//                    result.add(candidateRange);
//                }
//
//                var mergeCandidate = result.stream().filter(candidateRange::canMerge).findFirst();
//
//                if (mergeCandidate.isPresent()) {
//                    result.remove(mergeCandidate.get());
//                    result.add(candidateRange.merge(mergeCandidate.get()));
//                } else {
//                    result.add(candidateRange);
//                }
                result.add(candidateRange);
            }

            return result;
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

            var reliableSensors = sensors.stream()
                    .filter(s ->
                            s.closestBeacon.x >= 0 && s.closestBeacon.x <= maxBound &&
                                    s.closestBeacon.y >= 0 && s.closestBeacon.y <= maxBound
                    ).collect(Collectors.toSet());
//            var candidateLocations = new HashSet<Location>();
//
//            for (var y = 0L; y <= maxBound; y++) {
//                for (var x = 0L; x <= maxBound; x++) {
//                    candidateLocations.add(new Location(x, y));
//                }
//            }

            for (var y = 0; y <= maxBound; y++) {
                for (var x = 0; x <= maxBound; x++) {
                    final var location = new Location(x, y);

                    if (sensors.stream().allMatch(s -> s.manhattanDistance(location) > s.closestBeaconManhattanDistance())) {
                        return 4000000 * location.x + location.y;
                    }
                }
            }

            throw new RuntimeException("Shouldn't be here");
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

    private static class Beacon extends Location {
        Beacon(long x, long y) {
            super(x, y);
        }
    }
}
