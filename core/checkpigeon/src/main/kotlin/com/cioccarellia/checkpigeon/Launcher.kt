package com.cioccarellia.checkpigeon

import com.cioccarellia.checkpigeon.functions.IsTerminal
import com.cioccarellia.checkpigeon.logic.console.red
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.verifier.VerificationResult
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import com.cioccarellia.checkpigeon.model.State
import com.cioccarellia.checkpigeon.search.MiniMaxAlphaBeta

fun Engine.state(): State = State(expose(), status.gameStatus.turnColor, IsTerminal(expose(), status.gameStatus.turnColor))

fun main() {
    val engine = Engine(
        Player("Max", TileColor.WHITE, true) to Player("Min", TileColor.BLACK, false)
    );

    engine.stdoutBoard()


    var cycle = 1
    while (engine.status.gameStatus.isAlive) {
        val state = engine.state()

        val move = MiniMaxAlphaBeta(state)

        if (move == null) {
            println("no move found")
            break
        }

        println()
        println("[$cycle] -> ${engine.status.gameStatus.turnColor} executing ${move.humanMoveNotation()}")

        when (engine.applyMove(move)) {
            is VerificationResult.Passed -> {
                engine.stdoutBoardCoords(TileColor.WHITE, listOf(move.end))
                println()
            }

            is VerificationResult.Failed -> {
                println("Move rejected by engine".red())
                println("move_str=$move".red())
                println("move_numan=${move.humanMoveNotation()}".red())
                println()
                println()
                println()
                println()
                println()
                engine.stdoutBoardCoords(TileColor.WHITE, listOf(move.start, move.end) + move.captures)
                break
            }
        }

        cycle++
    }

}