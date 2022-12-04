package com.codingnagger.days;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day4 implements Day {
    @Override
    public String partOne(List<String> input) {
        return input.stream().map(Pair::new).filter(Pair::oneAssignmentFullyContainsTheOther).count() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return input.stream().map(Pair::new).filter(Pair::assignmentsOverlap).count() + "";
    }

    static class Pair {
        private final List<Assignment> assignments;

        Pair(String line) {
            this.assignments = Arrays.stream(line.split(",")).map(Assignment::new).collect(Collectors.toList());
        }

        private Assignment left() {
            return assignments.get(0);
        }
        private Assignment right() {
            return assignments.get(1);
        }

        boolean oneAssignmentFullyContainsTheOther() {
            return left().contains(right()) || right().contains(left());
        }

        boolean assignmentsOverlap() {
            return left().overlaps(right());
        }
    }

    static class Assignment {
        private final int minSection;
        private final int maxSection;

        Assignment(String value) {
            var sections = Arrays.stream(value.split("-")).map(Integer::parseInt).collect(Collectors.toList());
            this.minSection = Math.min(sections.get(0), sections.get(1));
            this.maxSection = Math.max(sections.get(0), sections.get(1));
        }

        public boolean contains(Assignment assignment) {
            return contains(assignment.minSection) && contains(assignment.maxSection);
        }

        public boolean contains(int section) {
            return minSection <= section && maxSection >= section;
        }

        public boolean overlaps(Assignment assignment) {
            return contains(assignment.minSection) || contains(assignment.maxSection) ||
                    assignment.contains(minSection) || assignment.contains(maxSection);
        }
    }
}
