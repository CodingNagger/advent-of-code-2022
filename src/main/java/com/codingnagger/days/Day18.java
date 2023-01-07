package com.codingnagger.days;

import java.util.*;
import java.util.stream.Collectors;

public class Day18 implements Day {
    private static String getCube(int x, int y, int z) {
        return String.format("%d,%d,%d", x, y, z);
    }

    @Override
    public String partOne(List<String> input) {
        return "" + externalSurfaceForCubes(input);
    }

    @Override
    public String partTwo(List<String> input) {
        return "" + externalSurfaceByFillingOutside(input);
    }

    private int externalSurfaceByFillingOutside(List<String> lavaCubes) {
        var integerInput = lavaCubes.stream()
                .map(this::toDimensions)
                .collect(Collectors.toList());

        var minX = integerInput.stream().mapToInt(i -> i[0]).min().orElseThrow();
        var minY = integerInput.stream().mapToInt(i -> i[1]).min().orElseThrow();
        var minZ = integerInput.stream().mapToInt(i -> i[2]).min().orElseThrow();

        var maxX = integerInput.stream().mapToInt(i -> i[0]).max().orElseThrow();
        var maxY = integerInput.stream().mapToInt(i -> i[1]).max().orElseThrow();
        var maxZ = integerInput.stream().mapToInt(i -> i[2]).max().orElseThrow();

        var queue = new ArrayDeque<String>();
        queue.add(getCube(minX, minY, minZ));

        var visited = new ArrayList<String>();
        var surfaceLava = new HashSet<String>();
        var filledAir = new HashSet<String>();

        while (!queue.isEmpty()) {
            var current = queue.poll();

            if (visited.contains(current)) {
                continue;
            }

            visited.add(current);

            var dimensions = toDimensions(current);
            var x = dimensions[0];
            var y = dimensions[1];
            var z = dimensions[2];

            if (x < minX - 2 || x > maxX + 2 || y < minY - 2 || y > maxY + 2 || z < minZ - 2 || z > maxZ + 2) {
                continue;
            }

            var airNeighbours = getNeighbours(x, y, z).stream().filter(c -> !lavaCubes.contains(c)).collect(Collectors.toList());
            var cubeNeighbours = getNeighbours(x, y, z).stream().filter(lavaCubes::contains).collect(Collectors.toList());

            if (cubeNeighbours.size() > 0) {
                surfaceLava.addAll(cubeNeighbours);
                filledAir.add(current);
            }

            queue.addAll(airNeighbours);
        }

        var outsideSurface = 0;

        for (var lava : surfaceLava) {
            var dimensions = toDimensions(lava);
            var x = dimensions[0];
            var y = dimensions[1];
            var z = dimensions[2];

            outsideSurface += (int) getNeighbours(x, y, z).stream().filter(c -> !lavaCubes.contains(c)).filter(filledAir::contains).count();
        }

        return outsideSurface;
    }

    private int externalSurfaceForCubes(List<String> cubes) {
        var integerInput = cubes.stream()
                .map(this::toDimensions)
                .collect(Collectors.toList());

        var minX = integerInput.stream().mapToInt(i -> i[0]).min().orElseThrow();
        var minY = integerInput.stream().mapToInt(i -> i[1]).min().orElseThrow();
        var minZ = integerInput.stream().mapToInt(i -> i[2]).min().orElseThrow();

        var maxX = integerInput.stream().mapToInt(i -> i[0]).max().orElseThrow();
        var maxY = integerInput.stream().mapToInt(i -> i[1]).max().orElseThrow();
        var maxZ = integerInput.stream().mapToInt(i -> i[2]).max().orElseThrow();

        var queue = new ArrayDeque<String>();
        queue.add(getCube(minX, minY, minZ));

        var visited = new ArrayList<String>();
        var exteriorSurface = 0;

        while (!queue.isEmpty()) {
            var current = queue.poll();

            if (visited.contains(current)) {
                continue;
            }

            visited.add(current);

            var dimensions = toDimensions(current);
            var x = dimensions[0];
            var y = dimensions[1];
            var z = dimensions[2];

            if (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ) {
                continue;
            }

            var neighbours = getNeighbours(x, y, z);
            var cubeNeighbours = getNeighbours(x, y, z).stream().filter(cubes::contains).collect(Collectors.toList());
            var cubeNeighboursCount = cubeNeighbours.size();

            if (cubes.contains(current)) {
                exteriorSurface += 6 - cubeNeighboursCount;
            }

            queue.addAll(neighbours);
        }

        return exteriorSurface;
    }

    private List<String> getNeighbours(int x, int y, int z) {

        return List.of(
                getCube(x + 1, y, z),
                getCube(x - 1, y, z),
                getCube(x, y + 1, z),
                getCube(x, y - 1, z),
                getCube(x, y, z + 1),
                getCube(x, y, z - 1)
        );
    }

    int[] toDimensions(String cubeDefinition) {
        return Arrays.stream(cubeDefinition.split(",")).mapToInt(Integer::parseInt).toArray();
    }
}
