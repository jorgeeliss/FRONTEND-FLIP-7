package edu.unac.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_history")
public class GameHistory {

    @Id
    private String gameId;

    private String winnerName;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "gameHistory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoundScoreRecord> scores = new ArrayList<>();

    public GameHistory() {
    }

    public GameHistory(String gameId, String winnerName) {
        this.gameId = gameId;
        this.winnerName = winnerName;
        this.createdAt = LocalDateTime.now();
    }

    public void addScoreRecord(RoundScoreRecord record) {
        this.scores.add(record);
        record.setGameHistory(this);
    }

    // Getters and Setters
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    public String getWinnerName() { return winnerName; }
    public void setWinnerName(String winnerName) { this.winnerName = winnerName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<RoundScoreRecord> getScores() { return scores; }
    public void setScores(List<RoundScoreRecord> scores) { this.scores = scores; }
}