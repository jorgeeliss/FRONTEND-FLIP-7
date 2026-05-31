package edu.unac.controller;

import edu.unac.dto.CreateGameRequest;
import edu.unac.dto.GameActionRequest;
import edu.unac.dto.GameResponse;
import edu.unac.dto.GameStateResponse;
import edu.unac.model.DeckFactory;
import edu.unac.model.Game;
import edu.unac.model.Player;
import edu.unac.repository.GameRepository;
import edu.unac.service.GameEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController {

    private final GameRepository gameRepository;
    private final DeckFactory deckFactory;
    private final GameEngineService gameEngine; // Añadimos el motor del juego

    // Inyectamos el GameEngineService en el constructor
    public GameController(GameRepository gameRepository, GameEngineService gameEngine) {
        this.gameRepository = gameRepository;
        this.gameEngine = gameEngine;
        this.deckFactory = new DeckFactory();
    }

    @PostMapping
    public ResponseEntity<GameResponse> createGame(@RequestBody CreateGameRequest request) {
        if (request.playerNames() == null || request.playerNames().size() < 4) {
            return ResponseEntity.badRequest()
                    .body(new GameResponse(null, "ERROR", "At least 4 players are required to start Flip7."));
        }

        Game newGame = new Game();
        for (String name : request.playerNames()) {
            newGame.addPlayer(new Player(name));
        }

        newGame.startGame(deckFactory);
        gameRepository.save(newGame);

        return ResponseEntity.ok(
                new GameResponse(newGame.getId(), newGame.getStatus().name(), "Game created and round started successfully!")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameStateResponse> getGameState(@PathVariable String id) {
        return gameRepository.findById(id)
                .map(game -> {
                    String currentPlayerId = null;
                    String currentPlayerName = null;
                    boolean isRoundFinished = true;

                    if (game.getCurrentRound() != null) {
                        isRoundFinished = game.getCurrentRound().isFinished();
                        if (!isRoundFinished) {
                            currentPlayerId = game.getCurrentRound().getCurrentPlayer().getId();
                            currentPlayerName = game.getCurrentRound().getCurrentPlayer().getName();
                        }
                    }

                    GameStateResponse response = new GameStateResponse(
                            game.getId(),
                            game.getStatus().name(),
                            isRoundFinished,
                            currentPlayerId,
                            currentPlayerName,
                            game.getPlayers()
                    );

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- NUEVO ENDPOINT PARA JUGAR ---
    @PostMapping("/{id}/action")
    public ResponseEntity<GameResponse> playAction(
            @PathVariable String id,
            @RequestBody GameActionRequest request) {

        // 1. Buscamos la partida
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Buscamos al jugador por su ID
        Player player = game.getPlayers().stream()
                .filter(p -> p.getId().equals(request.playerId()))
                .findFirst()
                .orElse(null);

        if (player == null) {
            return ResponseEntity.badRequest().body(new GameResponse(id, game.getStatus().name(), "Player not found"));
        }

        // 3. Ejecutamos la acción en el motor del juego
        try {
            if ("HIT".equalsIgnoreCase(request.action())) {
                gameEngine.playHit(game, player);
            } else if ("STAND".equalsIgnoreCase(request.action())) {
                gameEngine.playStand(game, player);
            } else {
                return ResponseEntity.badRequest().body(new GameResponse(id, game.getStatus().name(), "Invalid action. Use HIT or STAND."));
            }
        } catch (IllegalStateException e) {
            // Captura errores como "No es tu turno" o "La ronda ya terminó"
            return ResponseEntity.badRequest().body(new GameResponse(id, game.getStatus().name(), e.getMessage()));
        }

        return ResponseEntity.ok(new GameResponse(id, game.getStatus().name(), "Action executed successfully"));
    }

    // --- ENDPOINT TRAMPA SOLO PARA PRUEBAS ---
    @PostMapping("/{id}/force-win")
    public ResponseEntity<GameResponse> forceWin(@PathVariable String id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        // Ejecutamos la trampa
        gameEngine.forceWin(game);

        return ResponseEntity.ok(new GameResponse(
                id,
                game.getStatus().name(),
                "¡Trampa activada! El jugador 1 ha ganado y la partida se guardó en la BD."
        ));
    }

    // --- ENDPOINT PARA INICIAR SIGUIENTE RONDA ---
    @PostMapping("/{id}/next-round")
    public ResponseEntity<GameResponse> startNextRound(@PathVariable String id) {
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        if (game.isGameOver()) {
            return ResponseEntity.badRequest().body(new GameResponse(id, game.getStatus().name(), "The game is already over."));
        }

        game.startNewRound(deckFactory);
        gameRepository.save(game); // Guardamos el nuevo estado de la ronda

        return ResponseEntity.ok(new GameResponse(id, game.getStatus().name(), "New round started successfully"));
    }
}