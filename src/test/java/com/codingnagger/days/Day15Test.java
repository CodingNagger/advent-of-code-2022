package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day15Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day15.txt");
    private static final Day DAY = new Day15(true);

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("26");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("56000011");
    }
}
