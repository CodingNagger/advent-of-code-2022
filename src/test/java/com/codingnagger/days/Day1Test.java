package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day1Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day1.txt");
    private static final Day DAY_1 = new Day1();

    @Test
    void partOne_shouldReturnIncreaseCount() {
        String result = DAY_1.partOne(INPUT);

        assertThat(result).isEqualTo("");
    }

    @Test
    void partTwo_shouldReturnSlidingIncreaseCount() {
        String result = DAY_1.partTwo(INPUT);

        assertThat(result).isEqualTo("");
    }
}
