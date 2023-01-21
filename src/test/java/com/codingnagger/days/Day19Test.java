package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

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
    
    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo(null);
    }
}
