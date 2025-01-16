package com.cioccarellia.checkpigeon

import com.cioccarellia.checkpigeon.functions.IsTerminal
import com.cioccarellia.checkpigeon.logic.console.green
import com.cioccarellia.checkpigeon.logic.console.red
import com.cioccarellia.checkpigeon.logic.console.yellow
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.verifier.VerificationResult
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import com.cioccarellia.checkpigeon.model.State
import com.cioccarellia.checkpigeon.search.MiniMaxAlphaBeta

fun Engine.state(): State = State(expose(), status.gameStatus.turnColor, IsTerminal(expose(), status.gameStatus.turnColor))

const val CAP = 1000

fun main() {
    val engine = Engine(
        Player("Max", TileColor.WHITE, true) to Player("Min", TileColor.BLACK, false)
    )

    engine.stdoutBoard()


    var movements = 0;
    var captures = 0;
    var cycle = 1

    while (engine.status.gameStatus.isAlive) {
        val state = engine.state()

        val move = MiniMaxAlphaBeta(state)

        if (move == null) {
            println("No move found, game over")
            break
        }

        println()
        println("[$cycle] -> ${engine.status.gameStatus.turnColor} executing ${move.humanMoveNotation()}")

        when (engine.applyMove(move)) {
            is VerificationResult.Passed -> {
                engine.stdoutBoardCoords(TileColor.WHITE, listOf(move.end))
                println("White: d=" + (engine.expose().countPiecesWithTypeAndColor<Material.Dama>(TileColor.WHITE)) + ", D=" + engine.expose().countPiecesWithTypeAndColor<Material.Damone>(TileColor.WHITE))
                println("Black: d=" + (engine.expose().countPiecesWithTypeAndColor<Material.Dama>(TileColor.BLACK)) + ", D=" + engine.expose().countPiecesWithTypeAndColor<Material.Damone>(TileColor.BLACK))

                when (move.moveType) {
                    MoveType.Capture -> captures++
                    MoveType.Movement -> movements++
                }
                println()
            }

            is VerificationResult.Failed -> {
                println("Move rejected by engine".red())
                println("move_str=$move".red())
                println("move_human=${move.humanMoveNotation()}".red())
                println()
                println()
                println()
                println()
                println()
                engine.stdoutBoardCoords(TileColor.WHITE, listOf(move.start, move.end) + move.captures)
                break
            }
        }

        if (++cycle > CAP) {
            println("Reached $CAP runs, game over, draw".yellow())
            break
        }
    }


    engine.status.gameStatus.gameResult
    println("Movements: $movements\nCaptures: $captures".green())

}