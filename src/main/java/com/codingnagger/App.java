package com.codingnagger;

import com.codingnagger.days.Day;
import com.codingnagger.days.Day12;
import com.codingnagger.utils.InputLoader;

import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Advent of Code 2022");

        List<String> input = InputLoader.Load("day12.txt");

        Day day = new Day12();

        System.out.println("Part 1:");
        System.out.println(day.partOne(input));

        System.out.println("Part 2:");
        System.out.println(day.partTwo(input));
    }
}
