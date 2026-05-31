package edu.unac.dto;

public record GameResponse(
        String gameId,
        String status,
        String message
) {
}