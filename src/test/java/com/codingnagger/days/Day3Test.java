package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day3Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day3.txt");
    private static final Day DAY = new Day3();

    @Test
    void partOne_shouldReturnIncreaseCount() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("157");
    }

    @Test
    void partTwo_shouldReturnSlidingIncreaseCount() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("70");
    }
}
