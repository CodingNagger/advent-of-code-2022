package com.codingnagger.days;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day17 implements Day {
    @Override
    public String partOne(List<String> input) {
//        return new RockRotation(input.get(0)).letRocksFallAndReturnTowerHeight(2022L) + "";
        return new RockRotation(input.get(0)).letRocksFallAndReturnTowerHeightForLargeNumber(2022L) + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return new RockRotation(input.get(0)).letRocksFallAndReturnTowerHeightForLargeNumber(1000000000000L) + "";
    }

    static class RockRotation {
        private final Rock[] rocks = Arrays.stream(("####\n" +
                "\n" +
                ".#.\n" +
                "###\n" +
                ".#.\n" +
                "\n" +
                "..#\n" +
                "..#\n" +
                "###\n" +
                "\n" +
                "#\n" +
                "#\n" +
                "#\n" +
                "#\n" +
                "\n" +
                "##\n" +
                "##").split("\n\n")).map(Rock::new).toArray(Rock[]::new);
        private final String gases;
        private long[] latestRockTops;
        private int nextRockIndex = 0;
        private ArrayList<Location> printablePoints;
        private int gasCursor;
        private int currentRockIndex;
        private int left;
        private long top;

        public RockRotation(String gases) {
            this.gases = gases;
        }

        long letRocksFallAndReturnTowerHeight(long times) {
            this.latestRockTops = new long[]{-1, -1, -1, -1, -1, -1, -1};
            this.gasCursor = 0;
            this.printablePoints = new ArrayList<>();
            this.nextRockIndex = 0;

            var floor = IntStream.range(0, 7).mapToObj(x -> new Location(x, -1, '#')).collect(Collectors.toList());
            printablePoints.addAll(floor);

            for (var i = 0; i < times; i++) {
                nextRockFalls();
            }

            printablePoints.removeAll(floor);
            prettyPrint();

            return getTowerHeight();
        }

        long letRocksFallAndReturnTowerHeightForLargeNumber(long times) {
            this.latestRockTops = new long[]{-1, -1, -1, -1, -1, -1, -1};
            this.gasCursor = 0;
            this.printablePoints = new ArrayList<>();
            this.nextRockIndex = 0;

            var floor = IntStream.range(0, 7).mapToObj(x -> new Location(x, -1, '#')).collect(Collectors.toList());
            printablePoints.addAll(floor);

            List<Long> heightDiffs = new ArrayList<>();
            int cycleLength;
            while ((cycleLength = findCycleLength(heightDiffs)) == -1) {
                var previousHeight = getTowerHeight();
                nextRockFalls();
                var nextHeight = getTowerHeight();
                heightDiffs.add(nextHeight - previousHeight);
            }

            var preCycleIterationCount = heightDiffs.size() - 2L * cycleLength;

            if (times < preCycleIterationCount) {
                return heightDiffs.subList(0, (int) times).stream().mapToLong(Long::longValue).sum();
            }

            var remainingTimesAfterPreCycle = times - preCycleIterationCount;

            var preCyclesHeight = heightDiffs.subList(0, (int) preCycleIterationCount).stream().mapToLong(Long::longValue).sum();
            var fullCyclesDiffs = heightDiffs.subList((int) preCycleIterationCount, (int) preCycleIterationCount + cycleLength);

            var cycleMultiplier = remainingTimesAfterPreCycle / fullCyclesDiffs.size();
            var combinedCyclesHeight = cycleMultiplier * fullCyclesDiffs.stream().mapToLong(Long::longValue).sum();

            var remainingTimesPostCycle = remainingTimesAfterPreCycle - cycleMultiplier * fullCyclesDiffs.size();

            var postCyclesHeight = fullCyclesDiffs.subList(0, (int) remainingTimesPostCycle).stream().mapToLong(Long::longValue).sum();

            return preCyclesHeight + combinedCyclesHeight + postCyclesHeight;
        }

        private int findCycleLength(List<Long> sequence) {
            for (var length = gases.length(); length < sequence.size() / 2; length++) {
                boolean matches = true;
                for (var i = 0; i < length; i++) {
                    if (!Objects.equals(sequence.get(sequence.size() - 2 * length + i), sequence.get(sequence.size() - length + i))) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    return length;
                }
            }
            return -1;
        }

        private void prettyPrint() {
            var map = printableMap();

            var sb = new StringBuilder();

            var maxY = map.keySet().stream().mapToLong(l -> l).max().orElseThrow();

            for (var y = maxY; y >= 0; y--) {
                sb.append("\n");
                for (var x = 0; x < 7; x++) {
                    sb.append(map.get(y).get(x));
                }
            }

            System.out.println(sb);
        }

        private Map<Long, Map<Integer, Character>> printableMap() {
            var bigY = printablePoints.stream().map(l -> l.y).max(Comparator.naturalOrder()).orElseThrow();

            var minX = 0;
            var maxX = 6;
            var minY = 0L;
            var maxY = bigY + 3;

            var map1 = new HashMap<Long, Map<Integer, Character>>();

            for (var y = minY; y <= maxY; y++) {
                map1.put(y, new HashMap<>());

                for (var x = minX; x <= maxX; x++) {
                    map1.get(y).put(x, '.');
                }
            }

            for (var printable : printablePoints) {
                if (!map1.containsKey(printable.y)) {
                    continue;
                }
                map1.get(printable.y).put(printable.x, printable.value);
            }

            return map1;
        }

        Collection<Location> nextRockFalls() {
            var rock = nextRock();

            left = 2;
            top = 3 + rock.height() + getTowerHeight();


            while (noBottomCollision(rock, left, top)) { // can fall with shift calculation in there,
//                debugRockLocation(rock, left, top);

                top--;

//                debugRockLocation(rock, left, top);

                var gas = gases.charAt(gasCursor);

                if (gas == '<' && left > 0 && noSideCollision(rock, left - 1, top)) {
                    left--;
                } else if (gas == '>' && left + rock.width() < 7 && noSideCollision(rock, left + 1, top)) {
                    left++;
                }

//                debugRockLocation(rock, left, top);

                gasCursor = (gasCursor + 1) % gases.length();
            }


            updateTopRocks(rock, left, top);
            return addPrintablePoints(rock, left, top);
        }

        private void debugRockLocation(Rock rock, int left, int top) {
            Collection<Location> debugPoints;
            debugPoints = rock.solidLocations(top, left, '@');
            printablePoints.addAll(debugPoints);
            prettyPrint();
            printablePoints.removeAll(debugPoints);
        }

        private Collection<Location> addPrintablePoints(Rock rock, int left, long top) {
            var points = rock.solidLocations(top, left, (char) ('0' + currentRockIndex));
            this.printablePoints.addAll(points);
            return points;
        }

        private void updateTopRocks(Rock rock, int left, long top) {
            for (var j = 0; j < rock.width(); j++) {
                var x = left + j;

                latestRockTops[x] = Math.max(latestRockTops[x], top - rock.highestPoint[j]);
            }
        }

        private boolean noBottomCollision(Rock rock, int left, long top) {
            return IntStream.range(0, rock.lowestPoint.length)
                    .mapToObj(x -> new Location(left + x, top - rock.lowestPoint[x] - 1, '#'))
                    .noneMatch(printablePoints::contains);
        }

        private boolean noSideCollision(Rock rock, int left, long top) {
            return rock.solidLocations(top, left).stream().noneMatch(printablePoints::contains);
        }

        public long getTowerHeight() {
            return Arrays.stream(latestRockTops).max().orElse(-1) + 1;
        }

        Rock nextRock() {
            currentRockIndex = nextRockIndex;
            nextRockIndex = (nextRockIndex + 1) % rocks.length;
            return rocks[currentRockIndex];
        }

        public String prettyPrintLine(long y) {
            var line = new char[7];

            printableMap().get(y).forEach((x, c) -> line[x] = c);

            return new String(line);
        }
    }

    static class Rock {
        char[][] fill;
        int[] highestPoint;
        int[] lowestPoint;
        int[] leftmostPoint;
        int[] rightmostPoint;

        Rock(String description) {
            var lines = description.split("\n");
            fill = new char[lines.length][];

            for (var i = 0; i < height(); i++) {
                fill[i] = lines[i].toCharArray();
            }

            highestPoint = new int[width()];
            lowestPoint = new int[width()];
            leftmostPoint = new int[height()];
            rightmostPoint = new int[height()];

            for (var i = height() - 1; i >= 0; i--) {
                for (var j = 0; j < width(); j++) {
                    if ('#' == fill[i][j]) {
                        highestPoint[j] = i;
                    }
                }
            }

            for (var i = 0; i < height(); i++) {
                for (var j = 0; j < width(); j++) {
                    if ('#' == fill[i][j]) {
                        lowestPoint[j] = i;
                    }
                }
            }

            for (var i = height() - 1; i >= 0; i--) {
                for (var j = 0; j < width(); j++) {
                    if ('#' == fill[i][j]) {
                        rightmostPoint[i] = j;
                    }
                }
            }

            for (var i = height() - 1; i >= 0; i--) {
                for (var j = width() - 1; j >= 0; j--) {
                    if ('#' == fill[i][j]) {
                        leftmostPoint[i] = j;
                    }
                }
            }
        }

        public int height() {
            return fill.length;
        }

        public int width() {
            return fill[0].length;
        }

        public Collection<Location> solidLocations(long top, int left) {
            return solidLocations(top, left, '#');
        }

        public Collection<Location> solidLocations(long top, int left, char print) {
            var locations = new ArrayList<Location>();

            for (var y = 0; y < height(); y++) {
                for (var x = 0; x < width(); x++) {
                    if ('#' == fill[y][x]) {
                        locations.add(new Location(x + left, top - y, print));
                    }
                }
            }

            return locations;
        }
    }

    static class Location {
        char value;
        int x;
        long y;

        Location(int x, long y, char value) {
            this.x = x;
            this.y = y;
            this.value = value;
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
