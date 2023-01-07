package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class Day19Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day19.txt");
    private static final Day DAY = new Day19();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("33");
    }

    @ParameterizedTest
    @CsvSource({
            "9, Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.",
            "24, Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian."
    })
    void qualityLevel(long expectedQualityLevel, String definition) {
        var result = new Day19.Blueprint(definition).qualityLevel();

        assertThat(result).isEqualTo(expectedQualityLevel);
    }

    @ParameterizedTest
    @CsvSource({
            "true, geode:0;obsidian:1;clay:7;ore:3, geode:0;obsidian:0;clay:10;ore:1",
            "true, geode:1;obsidian:7;clay:3;ore:0, geode:0;obsidian:10;clay:1;ore:0",
            "true, geode:0;obsidian:2;clay:11;ore:2, geode:0;obsidian:1;clay:14;ore:2",
            "true, geode:1;obsidian:0;clay:0;ore:0, geode:0;obsidian:0;clay:0;ore:0",
    })
    void newerIsBetter(boolean expectedResult, String newer, String older) {
        var result = Day19.Blueprint.newerIsBetter(Day19.Blueprint.parseMap(newer), Day19.Blueprint.parseMap(older));

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void serialiseMap() {
        var geodeCount = (long) (100 * Math.random());
        var obsidianCount = (long) (100 * Math.random());
        var clayCount = (long) (100 * Math.random());
        var oreCount = (long) (100 * Math.random());

        var expectedResult = String.format("geode:%d;obsidian:%d;clay:%d;ore:%d", geodeCount, obsidianCount, clayCount, oreCount);

        assertThat(Day19.Blueprint.serialiseMap(
                Map.of(
                        "clay", clayCount,
                        "obsidian", obsidianCount,
                        "geode", geodeCount,
                        "ore", oreCount
                )
        )).isEqualTo(expectedResult);
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo(null);
    }
}
