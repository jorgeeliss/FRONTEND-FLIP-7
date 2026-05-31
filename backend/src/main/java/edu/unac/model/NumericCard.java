package edu.unac.model;

public record NumericCard(int value) implements Card {

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getName() {
        return String.valueOf(value);
    }
}