package com.cioccarellia.checkpigeon.input

import com.cioccarellia.checkpigeon.logic.board.CardinalDirection
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.board.File
import com.cioccarellia.checkpigeon.logic.model.board.FileLetter
import com.cioccarellia.checkpigeon.logic.model.board.Rank
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.move.symbol
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

object MoveParser {

    private fun parseCoordinate(first: Char, second: Char): Coordinate {
        val file = File.from(first)
        val rank = Rank.from(second)

        return Coordinate(file, rank)
    }

    object Blow {
        internal fun extractBlowPair(blowText: String): Pair<Coordinate, Coordinate> {
            check(blowText.contains("*"))
            val blownPiece = parseCoordinate(blowText.first(), blowText[1])
            val pieceThatShouldHaveBeenCaptured = parseCoordinate(blowText[3], blowText[4])

            return blownPiece to pieceThatShouldHaveBeenCaptured
        }
    }

    object Movement {
        internal fun extractMovementStartEndPair(movementText: String): Pair<Coordinate, Coordinate> {
            check(movementText.contains("-"))
            val start = parseCoordinate(movementText.first(), movementText[1])
            val end = parseCoordinate(movementText[3], movementText[4])

            return start to end
        }
    }

    // a1xb2
    // a7xb6xd4
    // a1xb2xc3xd4
    object Captures {


        internal fun extractCaptureStartEndPairV2(captureText: String): Pair<Coordinate, Coordinate> {
            check(captureText.contains("x"))
            val ctl = captureText.length

            // Start of capture list == moving piece
            val startingCoord = parseCoordinate(captureText.first(), captureText[1])

            // end of capture list == last captured
            val lastCapturedPiece = parseCoordinate(captureText[ctl - 2], captureText.last())

            val list = captureText.split("x").map {
                parseCoordinate(it[0], it[1])
            }

            // at the end of the cycle, where the piece lands
            var lastJumpPosition: Coordinate? = null
            // at the end of the cycle, the direction of the last capture
            var lastCaptureDirection: CardinalDirection? = null

            list.subList(1, list.size).forEachIndexed { i, coords ->
                val lastStart: Coordinate = when (i) {
                    0 -> startingCoord
                    else -> lastJumpPosition!!
                }

                val direction = CardinalDirection.infer(lastStart, coords)
                val landingCoordinates = direction.shiftedCoordinateBy1Diagonally(coords)

                lastJumpPosition = landingCoordinates
                lastCaptureDirection = direction
            }

            return startingCoord to lastJumpPosition!!
        }

        /**
         * Returns the list of captured pieces (excluding the beginning)
         * */
        internal fun extractCaptureList(captureText: String): List<Coordinate> {
            check(captureText.contains("x"))

            return captureText
                // Removes first item, which is the starting position
                .substring(3)
                .split("x")
                .map {
                    Coordinate(
                        File(
                            FileLetter.from(it[0])
                        ),
                        Rank.from(it[1])
                    )
                }
        }

        private fun inferNextCoordinate(center: Coordinate, capturer: Coordinate) = Coordinate(
            File(
                FileLetter.from(
                    center.file.letter.numeric + (center.file.letter.numeric - capturer.file.letter.numeric)
                )
            ),
            Rank(
                center.rank.number + (center.rank.number - capturer.rank.number)
            )
        )
    }

    // d4*e5_b4xe2
    fun convert(
        rawString: String?,
        playingColor: TileColor
    ): ParsedMove = try {
        val input = rawString!!.trim().lowercase()

        if (input.contains("_")) {
            // Composite move, contains blow and movement/capture
            val moveText = input.substring(6)
            val blowText = input.substring(0..4)

            // Checks
            check(blowText.length == 5)


            val blowPair = Blow.extractBlowPair(blowText)

            if (moveText.contains(MoveType.Movement.symbol())) {
                // Movement
                check(moveText.length == 5)
                val startEndTiles = Movement.extractMovementStartEndPair(moveText)

                ParsedMove.Success(
                    move = Move(
                        moveType = MoveType.Movement,
                        playingColor = playingColor,

                        start = startEndTiles.first,
                        end = startEndTiles.second,
                        captures = listOf(),
                        blows = blowPair
                    )
                )
            } else {
                // Captures
                check(moveText.length >= 5)
                val startEndTiles = Captures.extractCaptureStartEndPairV2(moveText)
                val captureList = Captures.extractCaptureList(moveText)

                ParsedMove.Success(
                    move = Move(
                        moveType = MoveType.Movement,
                        playingColor = playingColor,

                        start = startEndTiles.first,
                        end = startEndTiles.second,
                        captures = captureList,
                        blows = blowPair
                    )
                )
            }
        } else {
            val moveText = input

            if (moveText.contains(MoveType.Movement.symbol())) {
                // Movement
                check(moveText.length == 5)
                val startEndTiles = Movement.extractMovementStartEndPair(moveText)

                ParsedMove.Success(
                    move = Move(
                        moveType = MoveType.Movement,
                        playingColor = playingColor,

                        start = startEndTiles.first,
                        end = startEndTiles.second,
                        captures = listOf(),
                        blows = null
                    )
                )
            } else {
                // Captures
                check(moveText.length >= 5)
                val startEndTiles = Captures.extractCaptureStartEndPairV2(moveText)
                val captureList = Captures.extractCaptureList(moveText)

                ParsedMove.Success(
                    move = Move(
                        moveType = MoveType.Capture,
                        playingColor = playingColor,

                        start = startEndTiles.first,
                        end = startEndTiles.second,
                        captures = captureList,
                        blows = null
                    )
                )
            }
        }

    } catch (e: IndexOutOfBoundsException) {
        ParsedMove.Failure("Index out of bounds, malformed input.")
    } catch (e: Exception) {
        ParsedMove.Failure("String processing error.")
    }


}