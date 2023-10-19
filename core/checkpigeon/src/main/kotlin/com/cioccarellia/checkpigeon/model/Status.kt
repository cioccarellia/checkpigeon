package com.cioccarellia.checkpigeon.model

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

data class Status(
    val board: Board,
    val playerColor: TileColor,
    val isGameOver: Boolean
)
