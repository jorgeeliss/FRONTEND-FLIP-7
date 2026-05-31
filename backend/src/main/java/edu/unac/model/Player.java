package edu.unac.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Player {
    private final String id;
    private final String name;
    private int totalScore;


    private final List<Card> currentHand;
    private PlayerRoundState roundState;

    public Player(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.totalScore = 0;
        this.currentHand = new ArrayList<>();
        this.roundState = PlayerRoundState.PLAYING;
    }


    public void receiveCard(Card card) {
        this.currentHand.add(card);
    }

    public void stand() {
        this.roundState = PlayerRoundState.STANDING;
    }

    public void bust() {
        this.roundState = PlayerRoundState.BUSTED;
    }

    public void resetForNewRound() {
        this.currentHand.clear();
        this.roundState = PlayerRoundState.PLAYING;
    }

  
    public void addScore(int points) {
        this.totalScore += points;
    }

  
    public String getId() { return id; }
    public String getName() { return name; }
    public int getTotalScore() { return totalScore; }
    public List<Card> getCurrentHand() { return new ArrayList<>(currentHand); }
    public PlayerRoundState getRoundState() { return roundState; }
}