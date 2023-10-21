package com.cioccarellia.checkpigeon.logic.engine

import com.cioccarellia.checkpigeon.debug.CustomLogger
import com.cioccarellia.checkpigeon.ext.findPlayerWithColor
import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.console.red
import com.cioccarellia.checkpigeon.logic.engine.events.GameEvent
import com.cioccarellia.checkpigeon.logic.engine.game.GameResult
import com.cioccarellia.checkpigeon.logic.engine.internal.BoardPrinter
import com.cioccarellia.checkpigeon.logic.engine.status.EngineStatus
import com.cioccarellia.checkpigeon.logic.engine.verifier.MoveVerifier
import com.cioccarellia.checkpigeon.logic.engine.verifier.VerificationResult
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

/**
 * Checkpigeon game com.cioccarellia.checkpigeon.logic.getEngine
 * */
class Engine(
    /**
     * White and Black players
     * */
    val players: Pair<Player, Player>
) {
    /**
     * Output [GameEvent]-emitting [MutableSharedFlow]
     * */

    /**
     * Game board
     * */
    private val board = Board()

    /**
     * Engine Logger
     * */
    private val engineLogger = CustomLogger(tag = "Engine")

    /**
     * Game status
     * */
    val status = EngineStatus()

    init {
        check(players.first.color == TileColor.WHITE && players.second.color == TileColor.BLACK)

        status.onGameStarted()
    }

    private fun afterMoveSubmissionGameEngineLogic() {
        when {
            board.whitePieceCount == 0 -> {
                status.onGameEnded(
                    GameResult.Done(
                        winner = players.findPlayerWithColor(TileColor.BLACK),
                        loser = players.findPlayerWithColor(TileColor.WHITE),
                    )
                )
            }

            board.blackPieceCount == 0 -> {
                status.onGameEnded(
                    GameResult.Done(
                        winner = players.findPlayerWithColor(TileColor.WHITE),
                        loser = players.findPlayerWithColor(TileColor.BLACK),
                    )
                )
            }
        }
    }

    fun applyMove(move: Move): VerificationResult {
        return when (val verification = MoveVerifier.verifyMove(move, board, status.gameStatus)) {
            is VerificationResult.Passed -> {
                status.onMoveAccepted(verification.move, board)
                board.executeMoveForward(verification.move)

                afterMoveSubmissionGameEngineLogic()

                // applied move
                verification
            }

            is VerificationResult.Failed -> {
                status.onMoveRejected(verification.rejectionReason)

                // invalid move
                println("Invalid move submitted".red())
                verification
            }
        }
    }


    fun stdoutBoardCoords(
        colorPerspective: TileColor = status.gameStatus.turnColor,
        highlights: List<Coordinate> = emptyList()
    ) {
        stdoutBoard(colorPerspective, highlights.map { it.file to it.rank })
    }


    fun stdoutBoard(
        colorPerspective: TileColor = status.gameStatus.turnColor,
        highlights: Highlights = emptyList()
    ) {
        BoardPrinter.stdout(board, colorPerspective, highlights)
    }

    fun expose() = board
}