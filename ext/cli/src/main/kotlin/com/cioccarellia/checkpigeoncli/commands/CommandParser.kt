package com.cioccarellia.checkpigeoncli.commands

import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import java.io.File

object CommandParser {

    fun convertArgs(tokens: Array<String>): ParsedCommand = try {

        val command: Command = when (val rootCommand = tokens[0]) {
            "help" -> {
                require(tokens.size == 1) {
                    "help requires no arguments, received ${tokens.size} ($tokens)"
                }

                Command.Help
            }

            "exit" -> {
                require(tokens.size == 1) {
                    "exit requires no arguments, received ${tokens.size} ($tokens)"
                }

                Command.Exit
            }

            // import source=stdin "a1xb2,d3xd2"
            // import source=file /users/nnsns
            "import" -> {
                require(tokens.size == 3) {
                    "import requires 2 arguments, received ${tokens.size} ($tokens)"
                }

                val sourceToken = tokens[1]
                when (val lhs = sourceToken.split("=")[0]) {
                    "source" -> {
                        when (val rhs = sourceToken.split("=")[1]) {
                            "stdin" -> {
                                val input = tokens[2]
                                Command.ImportGame(
                                    source = Command.ImportGame.ImportSource.Stdin(input)
                                )
                            }
                            "file" -> {
                                val file = File(rhs)

                                require(file.exists() && file.isFile && file.canRead()) {
                                    "file $rhs has to exist and be readable"
                                }

                                Command.ImportGame(
                                    source = Command.ImportGame.ImportSource.Filesystem(
                                        file = file
                                    )
                                )
                            }
                            else -> throw IllegalArgumentException("source parameter \"$rhs\" is not valid. Allowed parameters: [stdin|file]")
                        }
                    }
                    else -> throw IllegalArgumentException("import only accepts \"source=...\" parameter. Received \"$lhs\"")
                }
            }

            // export destination=stdout
            // export destination=file
            "export" -> {
                require(tokens.size == 3) {
                    "export requires 2 arguments, received ${tokens.size} ($tokens)"
                }

                val destinationToken = tokens[1]

                when (val lhs = destinationToken.split("=")[0]) {
                    "source" -> {

                        when (val rhs = destinationToken.split("=")[1]) {
                            "stdout" -> {
                                Command.ExportGame(
                                    destination = Command.ExportGame.ExportDestination.STDOUT
                                )
                            }
                            "file" -> {
                                Command.ExportGame(
                                    destination = Command.ExportGame.ExportDestination.FILE
                                )
                            }
                            else -> throw IllegalArgumentException("destination parameter \"$rhs\" is not valid. Allowed parameters: [stdout|file]")
                        }
                    }
                    else -> throw IllegalArgumentException("export only accepts \"destination=...\" parameter. Received \"$lhs\"")
                }
            }

            // play mode=hh black=Apium white=Checkpigeon
            "play" -> {
                require(tokens.size == 4) {
                    "export requires 3 arguments, received ${tokens.size} ($tokens)"
                }

                val gameMode = tokens[1]

                when (val lhs = gameMode.split("=")[0]) {
                    "mode" -> {
                        val blackAlias = tokens[2].split("=")[1]
                        val whiteAlias = tokens[3].split("=")[1]

                        when (val rhs = gameMode.split("=")[1]) {
                            "hh" -> {
                                Command.CreateGame.GameHumanVsHuman(
                                    blackPlayer = Player(blackAlias, TileColor.BLACK, true),
                                    whitePlayer = Player(whiteAlias, TileColor.WHITE, true),
                                )
                            }
                            "he" -> {
                                Command.CreateGame.GameHumanVsEngine(
                                    blackPlayer = Player(blackAlias, TileColor.BLACK, false),
                                    whitePlayer = Player(whiteAlias, TileColor.WHITE, false),
                                )
                            }
                            "ee" -> {
                                Command.CreateGame.GameEngineVsEngine(
                                    blackPlayer = Player(blackAlias, TileColor.BLACK, false),
                                    whitePlayer = Player(whiteAlias, TileColor.WHITE, false),
                                )
                            }
                            else -> throw IllegalArgumentException("destination parameter \"$rhs\" is not valid. Allowed parameters: [hh|he|ee]")
                        }
                    }
                    else -> throw IllegalArgumentException("play only accepts \"mode=...\" parameter. Received \"$lhs\"")
                }
            }

            else -> Command.Help
        }

        ParsedCommand.Success(command)
    } catch (assertion: IllegalArgumentException) {
        ParsedCommand.Failure(assertion.message ?: "argument error")
    } catch (e: Exception) {
        ParsedCommand.Failure("generic failure")
    }

    fun convert(rawInput: String?): ParsedCommand {
        require(rawInput != null)

        val tokens = rawInput.split(" ").toTypedArray()
        return convertArgs(tokens)
    }
}