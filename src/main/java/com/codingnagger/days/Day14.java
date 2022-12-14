package com.codingnagger.days;

import java.util.*;
import java.util.stream.Collectors;

public class Day14 implements Day {
    @Override
    public String partOne(List<String> input) {
        return new Cave(input, false, 0).countMaxNonInfinitySand() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return new Cave(input, true, 2).countMaxNonInfinitySand() + "";
    }

    enum Material {
        AIR,
        ROCK,
        SAND
    }

    static class Cave {
        final Map<Location, Material> map;
        final Location topLeft;
        final Location bottomRight;
        private final Location floorCenter;

        Cave(List<String> input, boolean hasFloor, int floorDiff) {
            map = new HashMap<>();

            for (var line : input) {
                for (var rock : parseRockLine(line)) {
                    map.put(rock, Material.ROCK);
                }
            }

            var minX = map.keySet().stream().map(l -> l.x).min(Comparator.naturalOrder()).get();
            var maxX = map.keySet().stream().map(l -> l.x).max(Comparator.naturalOrder()).get();
            var minY = 0;
            var maxY = map.keySet().stream().map(l -> l.y).max(Comparator.naturalOrder()).get();

            for (var y = minY; y <= maxY; y++) {
                for (var x = minX; x <= maxX; x++) {
                    var location = new Location(x, y);

                    if (!map.containsKey(location)) {
                        map.put(location, Material.AIR);
                    }
                }
            }

            topLeft = new Location(minX, minY);
            bottomRight = new Location(maxX, maxY);
            floorCenter = hasFloor ? new Location(0, maxY + floorDiff) : null;
        }

        private static List<Location> parseRockLine(String line) {
            var rockLine = new ArrayList<Location>();
            var turns = Arrays.stream(line.split(" -> ")).map(l -> l.split(",")).map(s -> Arrays.stream(s).mapToInt(Integer::parseInt).toArray()).collect(Collectors.toList());

            for (var i = 0; i < turns.size() - 1; i++) {
                var currentLocation = turns.get(i);
                var nextLocation = turns.get(i + 1);

                if (currentLocation[0] == nextLocation[0]) {
                    var yStart = Math.min(currentLocation[1], nextLocation[1]);
                    var yEnd = Math.max(currentLocation[1], nextLocation[1]);
                    for (var y = yStart; y <= yEnd; y++) {
                        rockLine.add(new Location(currentLocation[0], y));
                    }
                }
                if (currentLocation[1] == nextLocation[1]) {
                    var xStart = Math.min(currentLocation[0], nextLocation[0]);
                    var xEnd = Math.max(currentLocation[0], nextLocation[0]);
                    for (var x = xStart; x <= xEnd; x++) {
                        rockLine.add(new Location(x, currentLocation[1]));
                    }
                }
            }

            return rockLine;
        }

        public long countMaxNonInfinitySand() {
            var sandSource = new Location(500, 0);
            var nextSandLocation = findNextInKnownMapSandLocation(sandSource);

            while (nextSandLocation != null) {
                map.put(nextSandLocation, Material.SAND);
                nextSandLocation = findNextInKnownMapSandLocation(sandSource);
//                prettyPrint();
            }

            return map.values().stream().filter(m -> m == Material.SAND).count();
        }

        private void prettyPrint() {
            StringBuilder sb = new StringBuilder();

            var topLeft = new Location(
                    map.keySet().stream().map(l -> l.x).min(Comparator.naturalOrder()).get(),
                    map.keySet().stream().map(l -> l.y).min(Comparator.naturalOrder()).get()
            );

            var bottomRight = new Location(
                    map.keySet().stream().map(l -> l.x).max(Comparator.naturalOrder()).get(),
                    map.keySet().stream().map(l -> l.y).max(Comparator.naturalOrder()).get()
            );

            for (var y = topLeft.y; y <= bottomRight.y; y++) {
                for (var x = topLeft.x; x <= bottomRight.x; x++) {

                    char c;

                    if (map.get(new Location(x, y)) != null) {
                        switch (map.get(new Location(x, y))) {
                            case AIR:
                                c = x == 500 && y == 0 ? '+' : '.';
                                break;
                            case ROCK:
                                c = '#';
                                break;
                            case SAND:
                                c = 'o';
                                break;
                            default:
                                c = '?';
                                break;
                        }
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
        }

        private Location findNextInKnownMapSandLocation(Location sand) {
            var down = new Location(sand.x, sand.y + 1);
            var downLeft = new Location(sand.x - 1, sand.y + 1);
            var downRight = new Location(sand.x + 1, sand.y + 1);

            if (isSolid(sand)) {
                return null;
            }

            if (isSolid(down) && isSolid(downLeft) && isSolid(downRight)) { // can rest
                return sand;
            }

            if (map.get(down) == Material.AIR ||
                    (floorCenter != null && map.get(down) == null && down.y < floorCenter.y)) {
                return findNextInKnownMapSandLocation(down);
            }

            if (map.get(downLeft) == Material.AIR ||
                    (floorCenter != null && map.get(downLeft) == null && downLeft.y < floorCenter.y)) {
                return findNextInKnownMapSandLocation(downLeft);
            }

            if (map.get(downRight) == Material.AIR ||
                    (floorCenter != null && map.get(downRight) == null && downRight.y < floorCenter.y)) {
                return findNextInKnownMapSandLocation(downRight);
            }

            return null;
        }

        private boolean isSolid(Location location) {
            return map.get(location) == Material.SAND || map.get(location) == Material.ROCK
                    || (floorCenter != null && location.y == floorCenter.y);
        }
    }

    static class Location {
        int x, y;

        Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        static Location origin() {
            return new Location(0, 0);
        }

        double distance(Location location) {
            return Math.sqrt(Math.pow(x - location.x, 2) + Math.pow(y - location.y, 2));
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
}
