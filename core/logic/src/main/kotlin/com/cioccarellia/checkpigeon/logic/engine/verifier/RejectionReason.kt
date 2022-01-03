package com.cioccarellia.checkpigeon.logic.engine.verifier

enum class RejectionReason {
    /**
     * The game is over, but a move has been submitted anyways
     * */
    GENERAL_GAME_ALREADY_FINISHED,

    /**
     * The game turn and the move player do not match
     * */
    GENERAL_PLAYER_TURN_MISMATCH,

    /**
     * The move type is not coherent with the move data.
     *
     * Instances of this are:
     * - A move with MoveType=Capture has no captured elements
     * */
    GENERAL_MOVE_TYPE_DATA_INCOHERENCY,



    /**
     * Starting movement square is void
     * */
    MOVEMENT_START_SQUARE_EMPTY,
    CAPTURE_START_SQUARE_EMPTY,

    /**
     * End square is occupied
     * */
    MOVEMENT_FINAL_SQUARE_OCCUPIED,
    CAPTURE_FINAL_SQUARE_OCCUPIED,

    /**
     * Movement/landing is logically disallowed
     * */
    MOVEMENT_DISALLOWED_MOVEMENT,
    CAPTURE_DISALLOWED_CAPTURE,


    /**
     * Wrong piece selected
     * */
    MOVEMENT_WRONG_COLOR_PIECE,
    CAPTURE_WRONG_COLOR_PIECE,

    /**
     * Wrong captured list number
     * */
    CAPTURE_CAPTURED_PIECES_NUMBER_MISMATCH,


    /**
     * A non-valid promotion has been inserted in the move
     * */
    CAPTURE_INJECTED_PROMOTION,

    /**
     * A capture has to collect non-empty material
     * */
    CAPTURE_EMPTY_CAPTURE_MATERIAL,
    /**
     * A dama can not collect a damona
     * */
    CAPTURE_DAMONE_CAPTURE_MATERIAL,


    /**
     * Same-color piece was being captured
     * */
    CAPTURE_CANNIBALISM,



}