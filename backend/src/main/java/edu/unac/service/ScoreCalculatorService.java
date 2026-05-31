package edu.unac.service;
import edu.unac.model.Card;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
@Service
public class ScoreCalculatorService {

    private static final int BONUS_POINTS = 15;
    private static final int CARDS_FOR_BONUS = 7;

    public boolean hasDuplicate(List<Card> hand) {
        Set<Integer> seenValues = new HashSet<>();
        for (Card card : hand) {
            // Set.add() returns false if the element was already in the set
            if (!seenValues.add(card.getValue())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasReachedSevenDifferentCards(List<Card> hand) {
        if (hasDuplicate(hand)) {
            return false;
        }
        return hand.size() == CARDS_FOR_BONUS;
    }

    public int calculateScore(List<Card> hand) {
        if (hasDuplicate(hand)) {
            return 0; // Busted hands yield 0 points
        }

        int score = 0;
        Set<Integer> uniqueValues = new HashSet<>();

        for (Card card : hand) {
            score += card.getValue();
            uniqueValues.add(card.getValue());
        }

        // Flip7 Rule: 15 bonus points for having 7 different numerical cards
        if (uniqueValues.size() >= CARDS_FOR_BONUS) {
            score += BONUS_POINTS;
        }

        return score;
    }
}