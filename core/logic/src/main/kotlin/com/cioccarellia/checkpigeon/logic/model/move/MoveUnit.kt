package com.cioccarellia.checkpigeon.logic.model.move

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

abstract class MoveUnit {
    abstract val moveType: MoveType
    abstract val playingColor: TileColor

    abstract val start: Coordinate
    abstract val end: Coordinate

    abstract val captures: List<Coordinate>
    abstract val blows: Pair<Coordinate, Coordinate>
}