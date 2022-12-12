package com.codingnagger.days;

import java.util.*;
import java.util.stream.Collectors;

public class Day12 implements Day {
    @Override
    public String partOne(List<String> input) {
        return new ElevationMap(input).shortestPath() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return new ElevationMap(input).optimalStartShortestPath() + "";
    }


    class ElevationMap {
        private static final char START = 'S';
        private static final char END = 'E';
        private static final int MOVEMENT_COST = 1;
        private final char[][] elevation;
        private int[][] distances;
        private Position start;
        private Position end;

        public ElevationMap(List<String> input) {
            elevation = new char[input.size()][];
            var index = 0;

            for (var line : input) {
                elevation[index++] = line.toCharArray();
            }

            for (var i = 0; i < input.size(); i++) {
                elevation[i] = input.get(i).toCharArray();

                var startX = input.get(i).indexOf(START);

                if (startX > -1) {
                    start = new Position(startX, i, 0);
                }

                var endX = input.get(i).indexOf(END);

                if (endX > -1) {
                    end = new Position(endX, i, Integer.MAX_VALUE);
                }
            }
        }

        public int shortestPath() {
            return shortestPath(start);
        }

        public int optimalStartShortestPath() {
            var shortestPath = Integer.MAX_VALUE;

            for (var y = 0; y < elevation.length; y++) {
                for (var x = 0; x < elevation[y].length; x++) {
                    var position = new Position(x, y, 0);

                    if (getElevation(position) == 'a') {
                        shortestPath = Math.min(shortestPath, shortestPath(position));
                    }
                }
            }

            return shortestPath;
        }

        public int shortestPath(Position start) {
            distances = createDistances(start);

            PriorityQueue<Position> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.distance));
            queue.add(start);

            while (!queue.isEmpty()) {
                Position current = queue.poll();
                List<Position> visitableNeighbors = getNeighbors(current);

                for (Position neighbor : visitableNeighbors) {
                    int currentDistanceToNeighbor = distances[current.y][current.x] + MOVEMENT_COST;

                    if (distances[neighbor.y][neighbor.x] > currentDistanceToNeighbor) {
                        distances[neighbor.y][neighbor.x] = currentDistanceToNeighbor;
                        queue.add(new Position(neighbor.x, neighbor.y, currentDistanceToNeighbor));
                    }
                }
            }

            for (var line : distances) {
                System.out.println(Arrays.stream(line).map(v -> v == Integer.MAX_VALUE ? -1 : v).mapToObj(v -> "\t" + v).collect(Collectors.joining()));
            }

            return distances[end.y][end.x];
        }

        private int[][] createDistances(Position start) {
            int[][] distances = new int[elevation.length][];

            for (int i = 0; i < distances.length; i++) {
                distances[i] = new int[elevation[i].length];
                Arrays.fill(distances[i], Integer.MAX_VALUE);
            }

            distances[start.y][start.x] = 0;

            return distances;
        }

        public boolean canMoveTo(Position current, Position target) {
            return getElevation(current) + 1 >= getElevation(target);
        }

        private char getElevation(Position position) {
            var positionElevation = elevation[position.y][position.x];

            if (positionElevation == END) {
                positionElevation = 'z';
            }

            if (positionElevation == START) {
                positionElevation = 'a';
            }

            return positionElevation;
        }

        private List<Position> getNeighbors(Position current) {
            int x = current.x;
            int y = current.y;
            int minX = Math.max(0, x - 1);
            int maxX = Math.min(elevation[y].length - 1, x + 1);
            int minY = Math.max(0, y - 1);
            int maxY = Math.min(elevation.length - 1, y + 1);

            List<Position> neighbors = new ArrayList<>();

            if (y != minY && canMoveTo(current, new Position(x, minY, distances[minY][x]))) {
                neighbors.add(new Position(x, minY, distances[minY][x]));
            }

            if (y != maxY && canMoveTo(current, new Position(x, maxY, distances[maxY][x]))) {
                neighbors.add(new Position(x, maxY, distances[maxY][x]));
            }

            if (x != minX && canMoveTo(current, new Position(minX, y, distances[y][minX]))) {
                neighbors.add(new Position(minX, y, distances[y][minX]));
            }

            if (x != maxX && canMoveTo(current, new Position(maxX, y, distances[y][maxX]))) {
                neighbors.add(new Position(maxX, y, distances[y][maxX]));
            }

            return neighbors;
        }
    }

    public class Position {
        private final int x, y, distance;

        public Position(int x, int y, int distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x && y == position.y && distance == position.distance;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, distance);
        }
    }
}
