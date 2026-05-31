package edu.unac.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {
    private static final int SCORE_TO_WIN = 200;
    private static final int MIN_PLAYERS = 4;

    private final String id;
    private final List<Player> players;
    private Round currentRound;
    private GameStatus status;
    private int startingPlayerIndex;

    // NUEVO: Contador de rondas
    private int roundCounter;

    public Game() {
        this.id = UUID.randomUUID().toString();
        this.players = new ArrayList<>();
        this.status = GameStatus.WAITING_FOR_PLAYERS;
        this.startingPlayerIndex = 0;
        this.roundCounter = 0;
    }

    public void addPlayer(Player player) {
        if (status != GameStatus.WAITING_FOR_PLAYERS) {
            throw new IllegalStateException("Cannot add players after the game has started.");
        }
        players.add(player);
    }

    public void startGame(DeckFactory deckFactory) {
        if (players.size() < MIN_PLAYERS) {
            throw new IllegalStateException("At least " + MIN_PLAYERS + " players are required to start.");
        }
        this.status = GameStatus.IN_PROGRESS;
        startNewRound(deckFactory);
    }

    public void startNewRound(DeckFactory deckFactory) {
        if (isGameOver()) {
            this.status = GameStatus.FINISHED;
            return;
        }

        this.roundCounter++; // Incrementamos la ronda
        Deck deck = deckFactory.createStandardDeck();
        this.currentRound = new Round(deck, players, startingPlayerIndex);

        this.startingPlayerIndex = (this.startingPlayerIndex + 1) % players.size();
    }

    public boolean isGameOver() {
        return players.stream().anyMatch(p -> p.getTotalScore() >= SCORE_TO_WIN);
    }

    public Player getWinner() {
        if (!isGameOver()) {
            return null;
        }
        return players.stream()
                .max((p1, p2) -> Integer.compare(p1.getTotalScore(), p2.getTotalScore()))
                .orElse(null);
    }

    public void finishGame() {
        this.status = GameStatus.FINISHED;
    }

    public String getId() { return id; }
    public List<Player> getPlayers() { return new ArrayList<>(players); }
    public Round getCurrentRound() { return currentRound; }
    public GameStatus getStatus() { return status; }
    public int getRoundCounter() { return roundCounter; } // Getter de la ronda
}