package com.codingnagger;

import com.codingnagger.days.*;
import com.codingnagger.utils.InputLoader;

import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("Advent of Code 2022");

        List<String> input = InputLoader.Load("day7.txt");

        Day day = new Day7();

        System.out.println("Part 1:");
        System.out.println(day.partOne(input));

        System.out.println("Part 2:");
        System.out.println(day.partTwo(input));
    }
}
