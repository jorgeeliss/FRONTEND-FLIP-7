package edu.unac.model;

import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Stack;

public class Deck {
    private final Stack<Card> cards;

    public Deck(Stack<Card> cards) {
        this.cards = cards;
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public Card draw() {
        if (this.cards.isEmpty()) {
            throw new EmptyStackException();
        }
        return this.cards.pop();
    }

    public int getRemainingCardsCount() {
        return this.cards.size();
    }

    public boolean isEmpty() {
        return this.cards.isEmpty();
    }
}