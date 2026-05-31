package edu.unac.model;

import java.util.Stack;

public class DeckFactory {

    private static final int MAX_NUMERIC_VALUE = 12;

    /**
     * Creates a standard Flip7 deck with only numeric cards.
     * Rules:
     * - One '0'
     * - One '1'
     * - 'N' cards of value 'N' (for N >= 2)
     */
    public Deck createStandardDeck() {
        Stack<Card> initialCards = new Stack<>();

        // Add 0 and 1
        initialCards.push(new NumericCard(0));
        initialCards.push(new NumericCard(1));

        // Add the rest of the cards (2 to 12)
        for (int currentValue = 2; currentValue <= MAX_NUMERIC_VALUE; currentValue++) {
            for (int count = 0; count < currentValue; count++) {
                initialCards.push(new NumericCard(currentValue));
            }
        }

        Deck deck = new Deck(initialCards);
        deck.shuffle();

        return deck;
    }
}