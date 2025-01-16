package com.cioccarellia.checkpigeon.model

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

data class State(
    val board: Board,
    var playerColor: TileColor,
    val isGameOver: Boolean
)
