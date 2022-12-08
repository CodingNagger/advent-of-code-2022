package com.codingnagger.days;

import java.util.Arrays;
import java.util.List;

public class Day8 implements Day {
    @Override
    public String partOne(List<String> input) {
        return "" + countVisibleTrees(createMap(input));
    }

    @Override
    public String partTwo(List<String> input) {
        return "" + highestScenicScore(createMap(input));
    }

    int[][] createMap(List<String> input) {
        var map = new int[input.size()][];
        var index = 0;

        for (var line : input) {
            map[index++] = Arrays.stream(line.split("")).mapToInt(Integer::parseInt).toArray();
        }

        return map;
    }

    int countVisibleTrees(int[][] map) {
        var visibleCount = 0;

        for (var x = 0; x < map.length; x++) {
            for (var y = 0; y < map[x].length; y++) {
                if (isVisible(x, y, map)) {
                    visibleCount++;
                }
            }
        }

        return visibleCount;
    }

    int highestScenicScore(int[][] map) {
        var max = 0;

        for (var x = 0; x < map.length; x++) {
            for (var y = 0; y < map[x].length; y++) {
                if (isVisible(x, y, map)) {
                    max = Math.max(max, scenicScore(x, y, map));
                }
            }
        }

        return max;
    }

    private boolean isVisible(int x, int y, int[][] map) {
        if (x == 0 || y == 0 || x == map.length - 1 || y == map[x].length - 1) {
            return true;
        }

        return isVisibleFromTheWest(x, y, map)
                || isVisibleFromTheNorth(x, y, map)
                || isVisibleFromTheEast(x, y, map)
                || isVisibleFromTheSouth(x, y, map);
    }

    public int scenicScore(int x, int y, int[][] map) {
        return countVisibleFromTheWest(x, y, map)
                * countVisibleFromTheNorth(x, y, map)
                * countVisibleFromTheEast(x, y, map)
                * countVisibleFromTheSouth(x, y, map);
    }

    private int countVisibleFromTheSouth(int x, int y, int[][] map) {
        var cursor = y + 1;
        var visible = 0;

        while (cursor < map[x].length) {
            visible++;
            if (map[x][cursor++] >= map[x][y]) {
                break;
            }
        }

        return visible;
    }

    private int countVisibleFromTheEast(int x, int y, int[][] map) {
        var cursor = x + 1;
        var visible = 0;

        while (cursor < map.length) {
            visible++;
            if (map[cursor++][y] >= map[x][y]) {
                break;
            }
        }

        return visible;
    }

    private int countVisibleFromTheNorth(int x, int y, int[][] map) {
        var cursor = y - 1;
        var visible = 0;

        while (cursor >= 0) {
            visible++;
            if (map[x][cursor--] >= map[x][y]) {
                break;
            }
        }

        return visible;
    }

    private int countVisibleFromTheWest(int x, int y, int[][] map) {
        var cursor = x - 1;
        var visible = 0;

        while (cursor >= 0) {
            visible++;
            if (map[cursor--][y] >= map[x][y]) {
                break;
            }


        }

        return visible;
    }

    private boolean isVisibleFromTheSouth(int x, int y, int[][] map) {
        var cursor = y + 1;

        while (cursor < map[x].length) {
            if (map[x][cursor++] >= map[x][y]) {
                return false;
            }
        }

        return true;
    }

    private boolean isVisibleFromTheEast(int x, int y, int[][] map) {
        var cursor = x + 1;

        while (cursor < map.length) {
            if (map[cursor++][y] >= map[x][y]) {
                return false;
            }
        }

        return true;
    }

    private boolean isVisibleFromTheNorth(int x, int y, int[][] map) {
        var cursor = y - 1;

        while (cursor >= 0) {
            if (map[x][cursor--] >= map[x][y]) {
                return false;
            }
        }

        return true;
    }

    private boolean isVisibleFromTheWest(int x, int y, int[][] map) {
        var cursor = x - 1;

        while (cursor >= 0) {
            if (map[cursor--][y] >= map[x][y]) {
                return false;
            }
        }

        return true;
    }
}
