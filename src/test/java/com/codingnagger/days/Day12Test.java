package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day12Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day12.txt");
    private static final Day DAY = new Day12();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("31");
    }

    @Test
    void partOne_short() {
        assertThat(DAY.partOne(List.of("SxE"))).isEqualTo(Integer.MAX_VALUE + "");
        assertThat(DAY.partOne(List.of("SbcdefghijklmnopqrstuvwxyE"))).isEqualTo("25");
        assertThat(DAY.partOne(List.of("SbcdefghijklmnopqrstuvwxyzE"))).isEqualTo("26");
        assertThat(DAY.partOne(List.of("SbcdefghijklmnopqrstuvwyyE"))).isEqualTo(Integer.MAX_VALUE + "");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("29");
    }
}
