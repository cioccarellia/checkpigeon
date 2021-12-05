package com.cioccarellia.checkpigeoncli.commands

sealed class ParsedCommand {
    data class Success(
        val command: Command
    ) : ParsedCommand()

    data class Failure(
        val message: String
    ) : ParsedCommand()
}