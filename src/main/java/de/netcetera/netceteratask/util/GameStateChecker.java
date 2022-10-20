package de.netcetera.netceteratask.util;

import de.netcetera.netceteratask.domain.enums.GameResult;
import de.netcetera.netceteratask.domain.model.Game;
import de.netcetera.netceteratask.rest.exception.GameAlreadyFinishedException;
import de.netcetera.netceteratask.rest.exception.InvalidMoveException;
import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class GameStateChecker {

    public static final List<Set<Integer>> WINNING_COMBINATIONS = new ArrayList<>() {{
        add(new HashSet<>(Arrays.asList(1, 2, 3)));
        add(new HashSet<>(Arrays.asList(4, 5, 6)));
        add(new HashSet<>(Arrays.asList(7, 8, 9)));
        add(new HashSet<>(Arrays.asList(1, 4, 7)));
        add(new HashSet<>(Arrays.asList(2, 5, 8)));
        add(new HashSet<>(Arrays.asList(3, 6, 9)));
        add(new HashSet<>(Arrays.asList(1, 5, 9)));
        add(new HashSet<>(Arrays.asList(3, 5, 7)));
    }};

    public void checkMovesAndSetGameState(Game game) {
        String moves = game.getMoves().toString();
        List<Integer> playerOneMoves = new ArrayList<>(5);
        List<Integer> playerTwoMoves = new ArrayList<>(5);
        for (int i = 0; i < moves.length(); i++) {
            if (i % 2 == 0) {
                playerOneMoves.add(Integer.parseInt(moves.charAt(i) + ""));
            } else {
                playerTwoMoves.add(Integer.parseInt(moves.charAt(i) + ""));
            }
        }
        Collections.sort(playerOneMoves);
        Collections.sort(playerTwoMoves);
        if (hasPlayerWon(playerOneMoves)) {
            game.setResult(GameResult.FINISHED_GAME_PLAYER_ONE_WON);
        } else if (hasPlayerWon(playerTwoMoves)) {
            game.setResult(GameResult.FINISHED_GAME_PLAYER_TWO_WON);
        } else if (moves.length() == 9) {
            game.setResult(GameResult.FINISHED_GAME_TIE);
        }
    }

    public boolean hasPlayerWon(List<Integer> playerOneMoves) {
        return WINNING_COMBINATIONS.stream()
                .anyMatch(winningCombo -> {
                    if (new HashSet<>(playerOneMoves).containsAll(winningCombo)) {
                        return true;
                    } else {
                        return false;
                    }
                });
    }

    public void checkIfMoveIsValid(Game game, Integer move) throws InvalidMoveException, GameAlreadyFinishedException {
        if (GameResult.isFinished(game.getResult())) {
            throw new GameAlreadyFinishedException("This game has already finished. Go play another game.");
        } else if (move < 1 || move > 9) {
            throw new InvalidMoveException(String.format("Illegal move! Move %d is not valid. Valid moves are 1 - 9!", move));
        } else if (game.getMoves() != null && game.getMoves().toString().contains(move.toString())) {
            throw new InvalidMoveException(String.format("Illegal move! Move %d was already played!", move));
        }
    }

}
