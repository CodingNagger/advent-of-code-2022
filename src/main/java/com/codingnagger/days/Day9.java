package com.codingnagger.days;

import java.util.*;
import java.util.stream.Collectors;

public class Day9 implements Day {
    @Override
    public String partOne(List<String> input) {
        return new Parcour(ShortMotion.initial()).move(input).countUniqueTailPositions() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return new Parcour(LongMotion.initial()).move(input).countUniqueTailPositions() + "";
    }

    public interface Motion {
        Motion derive(int x, int y);

        Location tail();

        String print();

        default String prettyPrint() {
            return print();
        }

        default void prettyTailMap() {
        }
    }

    static class Parcour {
        Motion cursor;
        List<Motion> seriesOfMotions;

        Parcour(Motion start) {
            cursor = start;
        }

        Parcour move(List<String> input) {

            seriesOfMotions = new ArrayList<>(List.of(cursor));

            System.out.println("Init");

            for (var step : input) {
                var directionAndDistance = step.split(" ");
                var direction = directionAndDistance[0];
                var distance = Integer.parseInt(directionAndDistance[1]);

                for (var i = 1; i <= distance; i++) {
                    System.out.println(step + " - wip | \n" + cursor.prettyPrint() + "\n");

                    switch (direction) {
                        case "L":
                            cursor = cursor.derive(-1, 0);
                            break;
                        case "R":
                            cursor = cursor.derive(1, 0);
                            break;
                        case "U":
                            cursor = cursor.derive(0, -1);
                            break;
                        case "D":
                            cursor = cursor.derive(0, 1);
                            break;
                    }

                    seriesOfMotions.add(cursor);


                }

                System.out.println(step + " - done | \n" + cursor.prettyPrint() + "\n");
            }

            printPrettyTailMap();

            return this;
        }

        private void printPrettyTailMap() {

            var bigX = seriesOfMotions.stream().map(l -> l.tail().x).max(Comparator.naturalOrder()).get();
            var smallX = seriesOfMotions.stream().map(l -> l.tail().x).min(Comparator.naturalOrder()).get();
            var bigY = seriesOfMotions.stream().map(l -> l.tail().y).max(Comparator.naturalOrder()).get();
            var smallY = seriesOfMotions.stream().map(l -> l.tail().y).min(Comparator.naturalOrder()).get();

            var minX = Math.min(0, Math.min(bigX, smallX));
            var maxX = Math.max(0, Math.max(bigX, smallX));
            var minY = Math.min(0, Math.min(bigY, smallY));
            var maxY = Math.max(0, Math.max(bigY, smallY));

            StringBuilder sb = new StringBuilder();

            var map = new String[maxY - minY + 1][];

            for (var y = minY; y <= maxY; y++) {
                map[y - minY] = new String[maxX - minX + 1];

                for (var x = minX; x <= maxX; x++) {
                    map[y - minY][x - minX] = ".";
                }
            }

            for (var motionTail :
                    seriesOfMotions.stream().map(Motion::tail).collect(Collectors.toList())) {
                map[motionTail.y - minY][motionTail.x - minX] = "#";
            }

            map[-minY][-minX] = "s";

            for (var y = minY; y <= maxY; y++) {
                sb.append("\n");
                for (var x = minX; x <= maxX; x++) {
                    sb.append(map[y - minY][x - minX]);
                }
            }

            System.out.print(sb);
        }

        public long countUniqueTailPositions() {
            return seriesOfMotions.stream().map(Motion::tail).distinct().count();
        }
    }

    static class LongMotion implements Motion {
        final List<Location> knots;

        LongMotion() {
            this(Collections.nCopies(10, Location.origin()));
        }

        LongMotion(List<Location> knots) {
            this.knots = knots;
        }

        static Motion initial() {
            return new LongMotion();
        }

        private static String getLabel(int i) {
            return i == 0 ? "H" : "" + i;
        }

        @Override
        public Motion derive(int x, int y) {
            var newRope = new ArrayList<Location>(knots.size());

            newRope.add(knots.get(0).derive(x, y));
            System.out.println("Move " + getLabel(0) + " from " + knots.get(0).print() + " to " + newRope.get(0).print());

            var i = 1;

            while (i < knots.size()) {
                var newParentKnot = newRope.get(i - 1);
                var existingParentKnot = knots.get(i - 1);

                var existingCurrentKnot = knots.get(i);
                var derivedCurrentKnot = existingCurrentKnot.derive(x, y);

                var diagonalNeighbourDistance = Math.sqrt(2);
                var horizontalNeighbourDistance = 1;

                if (newParentKnot.distance(existingCurrentKnot) <= diagonalNeighbourDistance) {
                    System.out.print("No movement 1");
                    newRope.add(existingCurrentKnot);
                } else if (newParentKnot.distance(existingParentKnot) == horizontalNeighbourDistance &&
                        existingParentKnot.distance(existingCurrentKnot) == diagonalNeighbourDistance) {
                    System.out.print("Replace Parent 1");
                    newRope.add(existingParentKnot);
                } else if (newParentKnot.distance(existingParentKnot) == diagonalNeighbourDistance) {
                    if (newParentKnot.x == existingCurrentKnot.x) {
                        System.out.print("X Alignment 1");
                        newRope.add(new Location(existingCurrentKnot.x, existingParentKnot.y));
                    } else if (newParentKnot.y == existingCurrentKnot.y) {
                        System.out.print("Y Alignment 2");
                        newRope.add(new Location(existingParentKnot.x, existingCurrentKnot.y));
                    } else if (existingParentKnot.distance(existingCurrentKnot) == horizontalNeighbourDistance) {
                        System.out.print("Vector 1");
                        var vector = new Location(newParentKnot.x - existingParentKnot.x, newParentKnot.y - existingParentKnot.y);
                        newRope.add(existingCurrentKnot.derive(vector.x, vector.y));
                    } else if (existingParentKnot.distance(existingCurrentKnot) == diagonalNeighbourDistance) {
                        System.out.print("Vector 2");
                        var vector = new Location(newParentKnot.x - existingParentKnot.x, newParentKnot.y - existingParentKnot.y);
                        newRope.add(existingCurrentKnot.derive(vector.x, vector.y));
                    } else {
                        System.out.print("Vector 3");
                        var vector = new Location(newParentKnot.x - existingParentKnot.x, newParentKnot.y - existingParentKnot.y);
                        newRope.add(existingCurrentKnot.derive(vector.x, vector.y));
                    }
                } else if (derivedCurrentKnot.equals(existingParentKnot)) {
                    System.out.print("Derivation 1");
                    newRope.add(derivedCurrentKnot);
                } else if (existingCurrentKnot.distance(newParentKnot) > diagonalNeighbourDistance) {
                    if (derivedCurrentKnot.distance(newParentKnot) == horizontalNeighbourDistance) {
                        System.out.print("Derivation 2");
                        newRope.add(derivedCurrentKnot);
                    } else if (derivedCurrentKnot.distance(newParentKnot) == diagonalNeighbourDistance) {
                        System.out.print("Derivation 3");
                        newRope.add(derivedCurrentKnot);
                    } else if (newParentKnot.x == existingCurrentKnot.x) {
                        System.out.print("X Alignment 2");
                        newRope.add(new Location(existingCurrentKnot.x, existingParentKnot.y));
                    } else if (newParentKnot.y == existingCurrentKnot.y) {
                        System.out.print("Y Alignment 2");
                        newRope.add(new Location(existingParentKnot.x, existingCurrentKnot.y));
                    } else {
                        System.out.print("No movement 2");
                        newRope.add(existingCurrentKnot);
                    }
                } else {
                    System.out.print("No movement 3");
                    newRope.add(existingCurrentKnot);
                }
                System.out.println(" - Move " + getLabel(i) + " from " + knots.get(i).print() + " to " + newRope.get(i).print());

                if (newRope.get(i).distance(newRope.get(i - 1)) > diagonalNeighbourDistance) {
                    throw new RuntimeException("All knots must be connected");
                }

                i++;
            }

            return new LongMotion(newRope);
        }

        @Override
        public Location tail() {
            return knots.get(9);
        }

        @Override
        public String print() {
            return knots.stream().map(s -> String.format("%s\t| ", s.print())).collect(Collectors.joining()) + tail().print();
        }

        @Override
        public String prettyPrint() {
            var bigX = knots.stream().map(l -> l.x).max(Comparator.naturalOrder()).get();
            var smallX = knots.stream().map(l -> l.x).min(Comparator.naturalOrder()).get();
            var bigY = knots.stream().map(l -> l.y).max(Comparator.naturalOrder()).get();
            var smallY = knots.stream().map(l -> l.y).min(Comparator.naturalOrder()).get();

            var minX = Math.min(0, Math.min(bigX, smallX));
            var maxX = Math.max(0, Math.max(bigX, smallX));
            var minY = Math.min(0, Math.min(bigY, smallY));
            var maxY = Math.max(0, Math.max(bigY, smallY));

            StringBuilder sb = new StringBuilder();

            var map = new String[maxY - minY + 1][];

            for (var y = minY; y <= maxY; y++) {
                map[y - minY] = new String[maxX - minX + 1];

                for (var x = minX; x <= maxX; x++) {
                    map[y - minY][x - minX] = ".";
                }
            }

            map[-minY][-minX] = "s";

            for (var i = knots.size() - 1; i >= 0; i--) {
                map[knots.get(i).y - minY][knots.get(i).x - minX] = getLabel(i);
            }

            for (var y = minY; y <= maxY; y++) {
                sb.append("\n");
                for (var x = minX; x <= maxX; x++) {
                    sb.append(map[y - minY][x - minX]);
                }
            }

            return sb.toString();
        }
    }

    static class ShortMotion implements Motion {
        final Location head, tail;

        ShortMotion(Location head, Location tail) {
            this.head = head;
            this.tail = tail;
        }

        static ShortMotion initial() {
            return new ShortMotion(Location.origin(), Location.origin());
        }

        @Override
        public ShortMotion derive(int x, int y) {
            var newHead = new Location(x + head.x, y + head.y);

            if (newHead.distance(tail) > Math.sqrt(2)) {
                return new ShortMotion(newHead, head);
            } else {
                return new ShortMotion(newHead, tail);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ShortMotion motion = (ShortMotion) o;
            return Objects.equals(head, motion.head) && Objects.equals(tail, motion.tail);
        }

        @Override
        public int hashCode() {
            return Objects.hash(head, tail);
        }

        @Override
        public Location tail() {
            return tail;
        }

        @Override
        public String print() {
            return head.print() + " - " + tail.print();
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

        public Location derive(int x, int y) {
            return new Location(this.x + x, this.y + y);
        }

        public String print() {
            return String.format("(%d ; %d) ", x, y);
        }
    }
}
