package edu.unac.repository;

import edu.unac.model.Game;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class GameRepository {
    private final Map<String, Game> games = new HashMap<>();

    public void save(Game game) {
        games.put(game.getId(), game);
    }

    public Optional<Game> findById(String id) {
        return Optional.ofNullable(games.get(id));
    }
}