package com.cioccarellia.checkpigeoncli.executors.import

import com.cioccarellia.checkpigeoncli.commands.Command.ImportGame
import com.cioccarellia.checkpigeoncli.executors.CommandExecutor

class CliImportExecutor(
    private val command: ImportGame
) : CommandExecutor<ImportGame>(command) {
    override fun execute() {
        TODO("Not yet implemented")
    }
}