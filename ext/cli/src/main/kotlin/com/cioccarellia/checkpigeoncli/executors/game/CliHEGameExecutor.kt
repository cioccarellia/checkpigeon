package com.cioccarellia.checkpigeoncli.executors.game

import com.cioccarellia.checkpigeon.ext.findPlayerWithColor
import com.cioccarellia.checkpigeon.input.CLICommand
import com.cioccarellia.checkpigeon.input.CommandParser
import com.cioccarellia.checkpigeon.input.ParsedMove
import com.cioccarellia.checkpigeon.logic.console.cyan
import com.cioccarellia.checkpigeon.logic.console.green
import com.cioccarellia.checkpigeon.logic.console.red
import com.cioccarellia.checkpigeon.logic.console.yellow
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import com.cioccarellia.checkpigeon.search.MiniMaxAlphaBeta
import com.cioccarellia.checkpigeon.state
import com.cioccarellia.checkpigeoncli.commands.Command.CreateGame.GameHumanVsEngine
import com.cioccarellia.checkpigeoncli.commands.readCLI
import com.cioccarellia.checkpigeoncli.executors.CommandExecutor
import java.util.*

/**
 * Game executor for human-to-human games, requiring continuous CLI input
 * */
class CliHEGameExecutor(
    private val command: GameHumanVsEngine
) : CommandExecutor<GameHumanVsEngine>(command) {

    private val engine = Engine(
        command.whitePlayer to command.blackPlayer
    )

    private val enginePlayer = if (command.whitePlayer.alias.lowercase(Locale.getDefault()).contains("ap")) {
        TileColor.WHITE
    } else if (command.blackPlayer.alias.lowercase(Locale.getDefault()).contains("ap")) {
        TileColor.BLACK
    } else {
        TileColor.entries.random()
    }

    private fun processInputMove(): CLICommand = with(engine.status.gameStatus) {
        val currentPlayer = engine.players.findPlayerWithColor(turnColor)
        val input = readCLI("Turn $turnNumber for $turnColor (${currentPlayer.alias}) | command")

        return@with CommandParser.parseCommand(input, turnColor)
    }

    override fun execute() {
        engine.stdoutBoard()
        println()

        while (engine.status.gameStatus.isAlive) {

            if (engine.status.gameStatus.turnColor == enginePlayer) {
                val currentPlayer = engine.players.findPlayerWithColor(engine.status.gameStatus.turnColor)
                print("Turn ${engine.status.gameStatus.turnNumber} for ${engine.status.gameStatus.turnColor} (${currentPlayer.alias}) " + "[automatic]".green() + " | command\" > ")

                val move = MiniMaxAlphaBeta(engine.state())

                if (move == null) {
                    print("Engine produced empty move".red())
                    return
                }


                println(move.humanMoveNotation().yellow())

                engine.applyMove(move)
                engine.stdoutBoardCoords(highlights = listOf(move.end))
                println()

            } else {
                when (val parsedMove = processInputMove()) {
                    is CLICommand.XMove -> when (val xmov = parsedMove.parsedMove) {
                        is ParsedMove.Success -> {
                            engine.applyMove(xmov.move)
                            engine.stdoutBoardCoords(highlights = listOf(xmov.move.end))
                            println()
                        }

                        is ParsedMove.Failure -> {
                            println("Error: ${xmov.message}")
                            println()
                        }
                    }

                    CLICommand.UnknownCommand -> {
                        println("Unknown command".red())
                        println()
                    }

                    CLICommand.DrawOffer -> TODO()
                    CLICommand.Resignation -> TODO()
                }
            }
        }

        println("Engine died. Bye bye".cyan())
    }


}