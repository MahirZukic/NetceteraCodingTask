package de.netcetera.netceteratask.domain.enums;

import de.netcetera.netceteratask.domain.model.Game;

public enum GameResult {

    GAME_CREATED,
    GAME_IN_PROGRESS,
    FINISHED_GAME_TIE,
    FINISHED_GAME_PLAYER_ONE_WON,
    FINISHED_GAME_PLAYER_TWO_WON;

    public static boolean isFinished(GameResult result) {
        return FINISHED_GAME_TIE == result || FINISHED_GAME_PLAYER_ONE_WON == result || FINISHED_GAME_PLAYER_TWO_WON == result;
    }

    public static boolean isFinished(Game game) {
        return isFinished(game.getResult());
    }
    public static boolean isNotFinished(GameResult result) {
        return GAME_CREATED == result || GAME_IN_PROGRESS == result;
    }
}
