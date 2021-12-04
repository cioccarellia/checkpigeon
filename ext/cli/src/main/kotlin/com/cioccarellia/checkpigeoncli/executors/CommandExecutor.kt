package com.cioccarellia.checkpigeoncli.executors

import com.cioccarellia.checkpigeoncli.commands.Command

abstract class CommandExecutor<C : Command>(
    private val executorCommand: C
) {
    abstract suspend fun execute()
}