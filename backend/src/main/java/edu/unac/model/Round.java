package edu.unac.model;

import java.util.List;

public class Round {
    private final Deck deck;
    private final List<Player> players;
    private int currentPlayerIndex;
    private boolean isFinished;

    public Round(Deck deck, List<Player> players, int startingPlayerIndex) {
        this.deck = deck;
        this.players = players;
        this.currentPlayerIndex = startingPlayerIndex;
        this.isFinished = false;

        resetPlayersState();
    }

    private void resetPlayersState() {
        for (Player player : players) {
            player.resetForNewRound();
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void moveToNextPlayer() {
        // Find the next player who is still ACTIVE
        int initialIndex = currentPlayerIndex;
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

            // If we looped back to the same player, everyone else is out
            if (currentPlayerIndex == initialIndex) {
                break;
            }
        } while (getCurrentPlayer().getRoundState() != PlayerRoundState.PLAYING);

        checkIfRoundIsFinished();
    }

    public void markAsFinished() {
        this.isFinished = true;
    }

    private void checkIfRoundIsFinished() {
        // A round is finished if NO players are ACTIVE
        boolean anyoneActive = players.stream()
                .anyMatch(p -> p.getRoundState() == PlayerRoundState.PLAYING);

        if (!anyoneActive) {
            this.isFinished = true;
        }
    }

    // Getters
    public boolean isFinished() { return isFinished; }
    public Deck getDeck() { return deck; }
}