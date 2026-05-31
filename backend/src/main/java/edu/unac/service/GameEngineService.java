package edu.unac.service;

import edu.unac.model.*;
import edu.unac.repository.GameHistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class GameEngineService {

    private final ScoreCalculatorService scoreCalculator;
    private final GameHistoryRepository gameHistoryRepository;

    public GameEngineService(ScoreCalculatorService scoreCalculator, GameHistoryRepository gameHistoryRepository) {
        this.scoreCalculator = scoreCalculator;
        this.gameHistoryRepository = gameHistoryRepository;
    }

    public void playHit(Game game, Player player) {
        Round currentRound = game.getCurrentRound();

        if (currentRound.isFinished()) {
            throw new IllegalStateException("The round is already finished.");
        }
        if (!currentRound.getCurrentPlayer().getId().equals(player.getId())) {
            throw new IllegalStateException("It is not this player's turn.");
        }

        // 1. El jugador saca una sola carta
        Card drawnCard = currentRound.getDeck().draw();
        player.receiveCard(drawnCard);

        // 2. Evaluamos si perdió o alcanzó el máximo automáticamente
        if (scoreCalculator.hasDuplicate(player.getCurrentHand())) {
            player.bust();
        } else if (scoreCalculator.hasReachedSevenDifferentCards(player.getCurrentHand())) {
            player.stand();
        }

        // 3. Evaluamos qué hacer con el turno
        checkRoundCompletionOrNextTurn(game);
    }

    public void playStand(Game game, Player player) {
        Round currentRound = game.getCurrentRound();

        if (currentRound.isFinished()) {
            throw new IllegalStateException("The round is already finished.");
        }
        if (!currentRound.getCurrentPlayer().getId().equals(player.getId())) {
            throw new IllegalStateException("It is not this player's turn.");
        }

        // El jugador decide no pedir más cartas en esta ronda
        player.stand();
        checkRoundCompletionOrNextTurn(game);
    }

    // --- NUEVO MÉTODO CENTRALIZADO ---
    private void checkRoundCompletionOrNextTurn(Game game) {
        Round currentRound = game.getCurrentRound();

        // Verificamos si aún queda alguien que no se haya plantado ni quemado
        boolean allFinished = game.getPlayers().stream()
                .noneMatch(p -> p.getRoundState() == PlayerRoundState.PLAYING);

        if (!allFinished) {
            // Si todavía hay gente jugando, OBLIGATORIAMENTE pasamos el turno al siguiente
            currentRound.moveToNextPlayer();
        } else {
            // Si ya todos se plantaron (o perdieron), se acaba la ronda para todos
            if (!currentRound.isFinished()) {
                currentRound.markAsFinished();

                GameHistory history = new GameHistory(game.getId(), "UNDETERMINED");

                for (Player p : game.getPlayers()) {
                    int pointsGainedInThisRound = 0;
                    if (p.getRoundState() == PlayerRoundState.STANDING) {
                        pointsGainedInThisRound = scoreCalculator.calculateScore(p.getCurrentHand());
                        p.addScore(pointsGainedInThisRound);
                    }

                    RoundScoreRecord scoreRecord = new RoundScoreRecord(
                            game.getRoundCounter(),
                            p.getName(),
                            pointsGainedInThisRound
                    );
                    history.addScoreRecord(scoreRecord);
                }

                if (game.isGameOver()) {
                    game.finishGame();
                    history.setWinnerName(game.getWinner().getName());
                    gameHistoryRepository.save(history);
                    System.out.println("🏆 Game results successfully saved to Database!");
                }
            }
        }
    }

    // --- MÉTODO TRAMPA SOLO PARA PRUEBAS ---
    public void forceWin(Game game) {
        if (game.getPlayers().isEmpty()) return;
        Player luckyPlayer = game.getPlayers().get(0);
        luckyPlayer.addScore(250);
        luckyPlayer.stand();
        checkRoundCompletionOrNextTurn(game);
    }
}