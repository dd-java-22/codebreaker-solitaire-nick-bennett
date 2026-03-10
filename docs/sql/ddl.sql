-- Generated 2026-03-10 16:28:09-0600 for database version 1

CREATE TABLE IF NOT EXISTS `game_summary`
(
    `game_summary_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `external_key`    TEXT                              NOT NULL,
    `pool`            TEXT                              NOT NULL,
    `pool_size`       INTEGER                           NOT NULL,
    `code_length`     INTEGER                           NOT NULL,
    `started`         INTEGER                           NOT NULL,
    `guess_count`     INTEGER                           NOT NULL,
    `solved`          INTEGER                           NOT NULL,
    `last_played`     INTEGER,
    `exact_matches`   INTEGER                           NOT NULL,
    `near_matches`    INTEGER                           NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS `index_game_summary_external_key` ON `game_summary` (`external_key`);

CREATE INDEX IF NOT EXISTS `index_game_summary_solved_started_last_played` ON `game_summary` (`solved`, `started`, `last_played`);

CREATE INDEX IF NOT EXISTS `index_game_summary_solved_pool_size_code_length_guess_count_last_played_started` ON `game_summary` (`solved`,
                                                                                                                                `pool_size`,
                                                                                                                                `code_length`,
                                                                                                                                `guess_count`,
                                                                                                                                `last_played`,
                                                                                                                                `started`);