package com.cioccarellia.checkpigeoncli.executors.game.hh

import com.cioccarellia.checkpigeon.ext.findPlayerWithColor
import com.cioccarellia.checkpigeon.input.CLICommand
import com.cioccarellia.checkpigeon.input.CommandParser
import com.cioccarellia.checkpigeon.input.ParsedMove
import com.cioccarellia.checkpigeon.logic.console.cyan
import com.cioccarellia.checkpigeon.logic.console.red
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeoncli.commands.Command.CreateGame.GameHumanVsHuman
import com.cioccarellia.checkpigeoncli.commands.readCLI
import com.cioccarellia.checkpigeoncli.executors.CommandExecutor

/**
 * Game executor for human-to-human games, requiring continuous CLI input
 * */
class CliHHGameExecutor(
    private val command: GameHumanVsHuman
) : CommandExecutor<GameHumanVsHuman>(command) {

    private val engine = Engine(
        command.whitePlayer to command.blackPlayer
    )

    private fun processInputMove(): CLICommand = with(engine.status.gameStatus) {
        val currentPlayer = engine.players.findPlayerWithColor(turnColor)
        val input = readCLI("Turn $turnNumber for $turnColor (${currentPlayer.alias}) | command")

        return@with CommandParser.parseCommand(input, turnColor)
    }

    override fun execute() {
        engine.stdoutBoard()

        while (engine.status.gameStatus.isAlive) {
            when (val parsedMove = processInputMove()) {
                is CLICommand.XMove -> when (val xmov = parsedMove.parsedMove) {
                    is ParsedMove.Success -> {
                        engine.applyMove(xmov.move)
                        engine.stdoutBoardCoords(highlights = listOf(xmov.move.end))
                    }

                    is ParsedMove.Failure -> {
                        println("Error: ${xmov.message}")
                    }
                }

                CLICommand.UnknownCommand -> {
                    println("Unknown command".red())
                }

                CLICommand.DrawOffer -> TODO()
                CLICommand.Resignation -> TODO()
            }
        }

        println("Engine died. Bye bye".cyan())
    }


}