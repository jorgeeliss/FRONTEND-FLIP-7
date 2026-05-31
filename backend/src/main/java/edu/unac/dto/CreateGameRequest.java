package edu.unac.dto;

import java.util.List;

public record CreateGameRequest(List<String> playerNames) {
}