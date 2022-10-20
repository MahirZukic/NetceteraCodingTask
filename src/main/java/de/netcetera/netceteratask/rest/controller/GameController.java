package de.netcetera.netceteratask.rest.controller;

import de.netcetera.netceteratask.domain.model.Game;
import de.netcetera.netceteratask.rest.dto.GameMoveDto;
import de.netcetera.netceteratask.rest.dto.NewGameDto;
import de.netcetera.netceteratask.rest.exception.GameAlreadyFinishedException;
import de.netcetera.netceteratask.rest.exception.InvalidMoveException;
import de.netcetera.netceteratask.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/game")
public class GameController {

    private final GameService service;

    @PostMapping(path = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createNewGameForPlayers(@RequestBody NewGameDto dto) {
        Game game = service.createGame(dto.getPlayerOne(), dto.getPlayerTwo());
        if (game != null) {
            return ResponseEntity.ok(game);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @PostMapping(path = "/play",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity playGameMove(@RequestBody GameMoveDto dto) {
        Game game = null;
        try {
            game = service.playNextMove(dto.getGameId(), dto.getMove());
        } catch (InvalidMoveException exception) {
            log.error(exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        } catch (GameAlreadyFinishedException exception) {
            log.error(exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        if (game != null) {
            return ResponseEntity.ok(game);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    @GetMapping(path = "/{gameId}")
    @GetMapping(path = "/{gameId}",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
//    @RequestMapping(value = "/{gameId}", method = RequestMethod.GET)
    public ResponseEntity findGameById(@PathVariable long gameId) {
        Game game = service.getGame(gameId);
        if (game != null) {
            return ResponseEntity.ok(game);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
