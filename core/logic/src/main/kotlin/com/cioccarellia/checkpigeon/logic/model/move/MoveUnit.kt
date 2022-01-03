package com.cioccarellia.checkpigeon.logic.model.move

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

abstract class MoveUnit {
    abstract val moveType: MoveType
    abstract val playingColor: TileColor

    abstract val start: Coordinate
    abstract val end: Coordinate

    abstract val captures: List<Coordinate>

    /**
     * Piece that should have taken the opposite piece and piece which should have been captured
     * */
    abstract val blows: Pair<Coordinate, Coordinate>?
    abstract val promotion: Coordinate?
}