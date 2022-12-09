package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day9Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day9.txt");
    private static final Day DAY = new Day9();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("13");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("1");
    }
    
    @Test
    void partTwo_large() {
        String result = DAY.partTwo(InputLoader.LoadTest("day9_large.txt"));

        assertThat(result).isEqualTo("36");
    }
}
