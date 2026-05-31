package edu.unac.dto;

public record GameActionRequest(
        String playerId,
        String action // Esperamos recibir "HIT" o "STAND"
) {
}