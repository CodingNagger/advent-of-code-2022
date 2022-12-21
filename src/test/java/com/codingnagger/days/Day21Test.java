package com.codingnagger.days;

import com.codingnagger.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day21Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day21.txt");
    private static final Day DAY = new Day21();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("152");
    }

    @Test
    void partTwo_swappedSub() {
        String result = DAY.partTwo(List.of("root: aaaa = bbbb", "aaaa: cccc - humn", "bbbb: 2", "humn: 10000", "cccc: 5"));

        assertThat(result).isEqualTo("3");
    }

    @Test
    void partTwo_swappedDiv() {
        String result = DAY.partTwo(List.of("root: aaaa = bbbb", "aaaa: cccc / humn", "bbbb: 2", "humn: 10000", "cccc: 10"));

        assertThat(result).isEqualTo("5");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("301");
    }
}
