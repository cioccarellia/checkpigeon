package com.cioccarellia.checkpigeoncli

import com.cioccarellia.checkpigeoncli.commands.Command
import com.cioccarellia.checkpigeoncli.commands.Command.*
import com.cioccarellia.checkpigeoncli.commands.Command.CreateGame.*
import com.cioccarellia.checkpigeoncli.commands.ParsedCommand
import com.cioccarellia.checkpigeoncli.commands.readCommand
import com.cioccarellia.checkpigeoncli.commands.readCommandFromArgs
import com.cioccarellia.checkpigeoncli.executors.export.CliExportExecutor
import com.cioccarellia.checkpigeoncli.executors.game.hh.CliHHGameExecutor
import com.cioccarellia.checkpigeoncli.executors.import.CliImportExecutor
import com.cioccarellia.checkpigeoncli.utils.CLIUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.system.exitProcess

/**
 * Core CLI
 * */
suspend fun main(args: Array<String>) {
    CLIUtil.welcomeScreen()

    var iteration = 1
    while (true) {
        fun assembleCommand() = if (iteration == 1 && args.isNotEmpty()) {
            readCommandFromArgs("ready", args)
        } else {
            readCommand("ready")
        }

        when (val parsedCommand = assembleCommand()) {
            is ParsedCommand.Failure -> {
                onParseFailure(parsedCommand.message)
            }
            is ParsedCommand.Success -> {
                val executed = CoroutineScope(Dispatchers.Default).async {
                    onSuccessfulCommandParse(parsedCommand.command)
                }

                executed.await()
            }
        }

        iteration++
    }
}

fun onParseFailure(message: String) {
    println("Could not parse command: $message")
}

suspend fun onSuccessfulCommandParse(command: Command) {
    when (command) {
        Exit -> {
            exitProcess(0)
        }

        Help -> {
            CLIUtil.helpPage()
        }

        is ExportGame -> CliExportExecutor(command).execute()
        is ImportGame -> CliImportExecutor(command).execute()

        is GameHumanVsHuman -> CliHHGameExecutor(command).execute()
        is GameHumanVsEngine -> TODO()
        is GameEngineVsEngine -> TODO()
    }
}