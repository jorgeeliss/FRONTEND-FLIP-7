package edu.unac.dto;

import edu.unac.model.Player;
import java.util.List;

public record GameStateResponse(
        String gameId,
        String status,
        boolean isRoundFinished,
        String currentPlayerId,
        String currentPlayerName,
        List<Player> players
) {
}