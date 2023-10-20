package com.cioccarellia.checkpigeon

import com.cioccarellia.checkpigeon.functions.IsTerminal
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import com.cioccarellia.checkpigeon.model.State
import com.cioccarellia.checkpigeon.search.MiniMaxAlphaBeta

fun Engine.state(): State = State(expose(), status.gameStatus.turnColor, IsTerminal(expose()))

fun main() {
    val engine = Engine(
        Player("Max", TileColor.WHITE, true) to Player("Min", TileColor.BLACK, false)
    );

    engine.stdoutBoard()


    while (engine.status.gameStatus.isAlive) {
        val state = engine.state()
        val move = MiniMaxAlphaBeta(state)

        if (move == null) {
            break
        }

        println()
        println("${engine.status.gameStatus.turnColor} executing ${move.humanMoveNotation()}")

        engine.applyMove(move)

        engine.stdoutBoardCoords(TileColor.WHITE, listOf(move.end))
        println()
    }


}