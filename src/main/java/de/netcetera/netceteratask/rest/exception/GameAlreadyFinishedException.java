package de.netcetera.netceteratask.rest.exception;

public class GameAlreadyFinishedException extends Throwable {
    public GameAlreadyFinishedException(String message) {
        super(message);
    }
}
