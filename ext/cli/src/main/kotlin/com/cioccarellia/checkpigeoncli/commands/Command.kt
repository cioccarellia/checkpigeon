package com.cioccarellia.checkpigeoncli.commands

import com.cioccarellia.checkpigeon.logic.model.player.Player
import java.io.File

sealed class Command {
    object Help : Command()

    object Exit : Command()


    /**
     * Imports a game
     * */
    data class ImportGame(
        val source: ImportSource,
    ) : Command() {
        sealed class ImportSource {
            class Stdin(val input: String) : ImportSource()
            class Filesystem(val file: File) : ImportSource()
        }
    }


    /**
     * Exports a game to a file which can be then loaded back in the game
     * */
    data class ExportGame(
        val destination: ExportDestination
    ) : Command() {
        enum class ExportDestination {
            STDOUT, FILE;
        }
    }


    sealed class CreateGame() : Command() {
        abstract val whitePlayer: Player
        abstract val blackPlayer: Player

        data class GameHumanVsHuman(
            override val whitePlayer: Player,
            override val blackPlayer: Player
        ) : CreateGame()

        data class GameHumanVsEngine(
            override val whitePlayer: Player,
            override val blackPlayer: Player
        ) : CreateGame()

        data class GameEngineVsEngine(
            override val whitePlayer: Player,
            override val blackPlayer: Player
        ) : CreateGame()
    }


}