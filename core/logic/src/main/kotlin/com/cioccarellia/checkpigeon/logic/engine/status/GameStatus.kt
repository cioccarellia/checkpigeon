package com.cioccarellia.checkpigeon.logic.engine.status

import com.cioccarellia.checkpigeon.logic.model.move.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

data class GameStatus(
    var moveNumber: Int = 1,
    var turnColor: TileColor = TileColor.WHITE,
    var alive: Boolean = true,
    var winner: TileColor? = null
) : EngineMoveReceiver {

    override fun onMoveAccepted(move: Move) {
        // Change turn and move
        when (move.playingColor) {
            TileColor.BLACK -> {
                turnColor = TileColor.WHITE
                moveNumber++
            }
            TileColor.WHITE -> {
                turnColor = TileColor.BLACK
            }
        }


    }
}