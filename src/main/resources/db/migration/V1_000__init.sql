CREATE TABLE IF NOT EXISTS player
(
    id                bigint    NOT NULL,
    username       VARCHAR(20)  NOT NULL,
    email      VARCHAR(40)  NOT NULL,

    CONSTRAINT player_id PRIMARY KEY (id)

);
CREATE UNIQUE INDEX player_username ON player (username);
CREATE UNIQUE INDEX player_email ON player (email);

CREATE TABLE IF NOT EXISTS game
(
    id                 bigint    NOT NULL,
    player_one         bigint    NOT NULL,
    player_two         bigint    NOT NULL,
    moves              int       NULL,
    game_result              int       NULL,
    created_at         timestamp NOT NULL DEFAULT now(),

    CONSTRAINT game_id PRIMARY KEY (id)
);
CREATE INDEX game_player_one ON game (player_one);
CREATE INDEX game_player_two ON game (player_two);
