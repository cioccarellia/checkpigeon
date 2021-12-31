package com.cioccarellia.checkpigeoncli.executors.game.input

import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

sealed class CLICommand() {
    class Move(val parsedMove: ParsedMove) : CLICommand()
    object DrawOffer : CLICommand()
    object Resignation : CLICommand()
    object UnknownCommand : CLICommand()
}

object CommandParser {
    fun parseCommand(
        rawString: String,
        turnColor: TileColor
    ): CLICommand {
        val command = rawString.trim()

        return when {
            command.startsWith("move") -> {
                val commandInput = command.removePrefix("move").removeSurrounding("(", ")")

                return CLICommand.Move(
                    parsedMove = MoveParser.convert(commandInput, turnColor)
                )
            }
            command.startsWith("draw") -> CLICommand.DrawOffer
            command.startsWith("resign") -> CLICommand.Resignation
            else -> CLICommand.UnknownCommand
        }
    }
}