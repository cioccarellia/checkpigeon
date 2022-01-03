package com.cioccarellia.checkpigeon.logic.engine.verifier

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.board.Direction
import com.cioccarellia.checkpigeon.logic.engine.status.GameStatus
import com.cioccarellia.checkpigeon.logic.engine.verifier.VerificationResult.Failed
import com.cioccarellia.checkpigeon.logic.engine.verifier.VerificationResult.Passed
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.board.Rank
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

object MoveVerifier {

    fun isPromotionSquare(
        color: TileColor,
        square: Coordinate
    ) = when (color) {
        TileColor.WHITE -> square.rank.number == 8
        TileColor.BLACK -> square.rank.number == 1
    }

    fun verifyMove(
        move: Move,
        board: Board,
        status: GameStatus
    ): VerificationResult {
        /*
        * We go first with some wide game-wise checks
        * */
        if (!status.isAlive) {
            // Game is dead
            return Failed(RejectionReason.GENERAL_GAME_ALREADY_FINISHED)
        }

        if (move.playingColor != status.turnColor) {
            // Played move's player is not the one who should be playing
            return Failed(RejectionReason.GENERAL_PLAYER_TURN_MISMATCH)
        }

        /*
        * Move-dependent checks
        * */
        return when (move.moveType) {
            MoveType.Movement -> verifyMovement(move, board, status)
            MoveType.Capture -> verifyCapture(move, board, status)
        }
    }


    /**
     * Tricky things:
     * - blows
     * - black and white move in opposite directions, so you have to flip the north when black plays
     * */
    private fun verifyMovement(
        move: Move,
        board: Board,
        status: GameStatus
    ): VerificationResult {
        val movingMaterial = board[move.start]
        val landingMaterial = board[move.end]

        if (landingMaterial != Material.Empty) {
            // landing square must be empty
            // we check starting square in branch
            return Failed(RejectionReason.MOVEMENT_FINAL_SQUARE_OCCUPIED)
        }

        return when (movingMaterial) {
            is Material.Dama -> {
                if (movingMaterial.color != status.turnColor) {
                    // Wrong color piece
                    return Failed(RejectionReason.MOVEMENT_WRONG_COLOR_PIECE)
                }

                // We can move by 1 square forward, left or right.
                if (status.turnColor == TileColor.WHITE) {
                    // With white, forward = north
                    val forwardLeftCheck = areCoordinatesCompatibleForMovement(move.start, move.end, Direction.NW)
                    val forwardRightCheck = areCoordinatesCompatibleForMovement(move.start, move.end, Direction.NE)

                    if (!forwardLeftCheck && !forwardRightCheck) {
                        // in case none of them is valid
                        return Failed(RejectionReason.MOVEMENT_DISALLOWED_MOVEMENT)
                    }
                } else {
                    // With black, forward = south
                    val forwardLeftCheck = areCoordinatesCompatibleForMovement(move.start, move.end, Direction.SW)
                    val forwardRightCheck = areCoordinatesCompatibleForMovement(move.start, move.end, Direction.SE)

                    if (!forwardLeftCheck && !forwardRightCheck) {
                        // in case none of them is valid
                        return Failed(RejectionReason.MOVEMENT_DISALLOWED_MOVEMENT)
                    }
                }

                // All checks have passed
                // At this point we check for promotion (only for a dama)
                if (isPromotionSquare(color = status.turnColor, square = move.end)) {
                    move.promotion = move.end
                } else if (move.promotion != null) {
                    return Failed(RejectionReason.CAPTURE_INJECTED_PROMOTION)
                }

                return Passed(move)
            }
            is Material.Damone -> {
                if (movingMaterial.color != status.turnColor) {
                    // Wrong color piece
                    return Failed(RejectionReason.MOVEMENT_WRONG_COLOR_PIECE)
                }


                // We can move by 1 square forward, left or right, or backward, left or right.
                // in this case we check them all, player can go backward and forward.
                val forwardLeftCheck = areCoordinatesCompatibleForMovement(move.start, move.end, Direction.NW)
                val forwardRightCheck = areCoordinatesCompatibleForMovement(move.start, move.end, Direction.NE)
                val backwardLeftCheck = areCoordinatesCompatibleForMovement(move.start, move.end, Direction.SW)
                val backwardRightCheck = areCoordinatesCompatibleForMovement(move.start, move.end, Direction.SE)

                when {
                    forwardLeftCheck -> {

                    }
                    forwardRightCheck -> {

                    }
                    backwardLeftCheck -> {

                    }
                    backwardRightCheck -> {

                    }
                    else -> {
                        return Failed(RejectionReason.MOVEMENT_DISALLOWED_MOVEMENT)
                    }
                }

                return Passed(move)
            }
            Material.Empty -> {
                return Failed(RejectionReason.MOVEMENT_START_SQUARE_EMPTY)
            }
        }
    }

    /**
     * Returns whether a movement is allowed for a certain coordinate, in order
     * not to fall of the board.
     * */
    private fun isMovementLegal(coordinate: Coordinate, direction: Direction) = when (direction) {
        // east
        Direction.NE -> coordinate.file.letter.numeric <= 7 && coordinate.rank.number <= 7
        Direction.SE -> coordinate.file.letter.numeric <= 7 && coordinate.rank.number >= 2

        // west
        Direction.SW -> coordinate.file.letter.numeric >= 2 && coordinate.rank.number >= 2
        Direction.NW -> coordinate.file.letter.numeric >= 2 && coordinate.rank.number <= 7
    }

    /**
     * Returns whether a 1-diagonal movement from [start] to [end] through the [direction] is legal and matches the given direction.
     * */
    private fun areCoordinatesCompatibleForMovement(start: Coordinate, end: Coordinate, direction: Direction) =
        if (isMovementLegal(start, direction)) {
            val theoreticalCoordinate = direction.shiftedCoordinateBy1Diagonally(start)

            end == theoreticalCoordinate
        } else false


    /**
     * Tricky things
     * - blows
     * - black and white move in opposite directions, so you have to flip the north when black plays
     *
     *
     *
     *
     *
     *
     * */
    private fun verifyCapture(
        move: Move,
        board: Board,
        status: GameStatus
    ): VerificationResult {
        if (move.moveType == MoveType.Capture && move.captures.isEmpty()) {
            // Captures nothing
            return Failed(RejectionReason.GENERAL_MOVE_TYPE_DATA_INCOHERENCY)
        }

        val attackingMaterial = board[move.start]
        val landingMaterial = board[move.end]

        if (landingMaterial != Material.Empty) {
            // landing square must be empty
            // we check starting square in branch
            return Failed(RejectionReason.CAPTURE_FINAL_SQUARE_OCCUPIED)
        }

        return when (attackingMaterial) {
            is Material.Dama -> {
                if (move.captures.size > 3) {
                    // Dama captures are capped to 3, because they move forward
                    return Failed(RejectionReason.CAPTURE_CAPTURED_PIECES_NUMBER_MISMATCH)
                }

                if (attackingMaterial.color != status.turnColor) {
                    // Wrong color piece
                    return Failed(RejectionReason.CAPTURE_WRONG_COLOR_PIECE)
                }

                var lastSuccessfulJumpLand: Coordinate? = null
                move.captures.forEachIndexed { jumpIndex, currentCaptureCoords ->
                    /*
                     * For each iteration, this is the starting square.
                     * */
                    val startJumpCoordinate: Coordinate = when (jumpIndex) {
                        0 -> move.start
                        else -> lastSuccessfulJumpLand!!
                    }

                    when (val capturedPiece = board[currentCaptureCoords]) {
                        is Material.Dama -> {
                            if (capturedPiece.color == status.turnColor) {
                                return Failed(RejectionReason.CAPTURE_CANNIBALISM)
                            }

                            if (status.turnColor == TileColor.WHITE) {
                                // With white, forward = north, right = east
                                val forwardLeftJumpCheck = areCoordinatesCompatibleForSingleCaptureJump(
                                    startJump = startJumpCoordinate,
                                    middleJump = currentCaptureCoords,
                                    jumpDirection = Direction.NW
                                )

                                val forwardRightJumpCheck = areCoordinatesCompatibleForSingleCaptureJump(
                                    startJump = startJumpCoordinate,
                                    middleJump = currentCaptureCoords,
                                    jumpDirection = Direction.NE
                                )

                                when {
                                    forwardLeftJumpCheck -> {
                                        val jumpDestination =
                                            Direction.NW.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
                                        lastSuccessfulJumpLand = jumpDestination
                                    }
                                    forwardRightJumpCheck -> {
                                        val jumpDestination =
                                            Direction.NE.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
                                        lastSuccessfulJumpLand = jumpDestination
                                    }
                                    else -> {
                                        return Failed(RejectionReason.CAPTURE_DISALLOWED_CAPTURE)
                                    }
                                }
                            } else {
                                // With black, forward = south, right = west
                                val forwardLeftJumpCheck = areCoordinatesCompatibleForSingleCaptureJump(
                                    startJump = startJumpCoordinate,
                                    middleJump = currentCaptureCoords,
                                    jumpDirection = Direction.SE
                                )

                                val forwardRightJumpCheck = areCoordinatesCompatibleForSingleCaptureJump(
                                    startJump = startJumpCoordinate,
                                    middleJump = currentCaptureCoords,
                                    jumpDirection = Direction.SW
                                )

                                when {
                                    forwardLeftJumpCheck -> {
                                        val jumpDestination =
                                            Direction.SE.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
                                        lastSuccessfulJumpLand = jumpDestination
                                    }
                                    forwardRightJumpCheck -> {
                                        val jumpDestination =
                                            Direction.SW.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
                                        lastSuccessfulJumpLand = jumpDestination
                                    }
                                    else -> {
                                        return Failed(RejectionReason.CAPTURE_DISALLOWED_CAPTURE)
                                    }
                                }
                            }
                        }
                        is Material.Damone -> return Failed(RejectionReason.CAPTURE_DAMONE_CAPTURE_MATERIAL)
                        Material.Empty -> return Failed(RejectionReason.CAPTURE_EMPTY_CAPTURE_MATERIAL)
                    }
                }

                // All checks have passed
                // At this point we check for promotion (only for a dama)
                if (isPromotionSquare(color = status.turnColor, square = move.end)) {
                    move.promotion = move.end
                } else if (move.promotion != null) {
                    return Failed(RejectionReason.CAPTURE_INJECTED_PROMOTION)
                }

                return Passed(move)
            }
            is Material.Damone -> {
                if (move.captures.size > 9) {
                    // Damona captures are capped to 9, because they eventually run out of targets
                    return Failed(RejectionReason.CAPTURE_CAPTURED_PIECES_NUMBER_MISMATCH)
                }

                if (attackingMaterial.color != status.turnColor) {
                    // Wrong color piece
                    return Failed(RejectionReason.CAPTURE_WRONG_COLOR_PIECE)
                }


                var lastSuccessfulJumpLand: Coordinate? = null
                move.captures.forEachIndexed { jumpIndex, currentCaptureCoords ->
                    /*
                     * For each iteration, this is the starting square.
                     * */
                    val startJumpCoordinate: Coordinate = when (jumpIndex) {
                        0 -> move.start
                        else -> lastSuccessfulJumpLand!!
                    }

                    fun damonaCheckFunction(): Failed? {
                        val forwardLeftJumpCheck = areCoordinatesCompatibleForSingleCaptureJump(
                            startJump = startJumpCoordinate,
                            middleJump = currentCaptureCoords,
                            jumpDirection = Direction.NW
                        )

                        val forwardRightJumpCheck = areCoordinatesCompatibleForSingleCaptureJump(
                            startJump = startJumpCoordinate,
                            middleJump = currentCaptureCoords,
                            jumpDirection = Direction.NE
                        )

                        val backwardRightJumpCheck = areCoordinatesCompatibleForSingleCaptureJump(
                            startJump = startJumpCoordinate,
                            middleJump = currentCaptureCoords,
                            jumpDirection = Direction.SE
                        )

                        val backwardLeftJumpCheck = areCoordinatesCompatibleForSingleCaptureJump(
                            startJump = startJumpCoordinate,
                            middleJump = currentCaptureCoords,
                            jumpDirection = Direction.SW
                        )


                        when {
                            forwardLeftJumpCheck -> {
                                val jumpDestination =
                                    Direction.NW.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
                                lastSuccessfulJumpLand = jumpDestination
                            }
                            forwardRightJumpCheck -> {
                                val jumpDestination =
                                    Direction.NE.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
                                lastSuccessfulJumpLand = jumpDestination
                            }
                            backwardLeftJumpCheck -> {
                                val jumpDestination =
                                    Direction.SW.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
                                lastSuccessfulJumpLand = jumpDestination
                            }
                            backwardRightJumpCheck -> {
                                val jumpDestination =
                                    Direction.SE.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
                                lastSuccessfulJumpLand = jumpDestination
                            }
                            else -> {
                                return Failed(RejectionReason.CAPTURE_DISALLOWED_CAPTURE)
                            }
                        }

                        return null
                    }

                    when (val capturedPiece = board[currentCaptureCoords]) {
                        is Material.Dama -> {
                            if (capturedPiece.color == status.turnColor) {
                                return Failed(RejectionReason.CAPTURE_CANNIBALISM)
                            }

                            damonaCheckFunction()?.let {
                                return it
                            }
                        }
                        is Material.Damone -> {
                            if (capturedPiece.color == status.turnColor) {
                                return Failed(RejectionReason.CAPTURE_CANNIBALISM)
                            }

                            damonaCheckFunction()?.let {
                                return it
                            }
                        }
                        Material.Empty -> return Failed(RejectionReason.CAPTURE_EMPTY_CAPTURE_MATERIAL)
                    }
                }

                return Passed(move)
            }
            Material.Empty -> {
                return Failed(RejectionReason.CAPTURE_START_SQUARE_EMPTY)
            }
        }

    }

    fun isJumpLegal(coordinate: Coordinate, direction: Direction) = when (direction) {
        // east
        Direction.NE -> coordinate.file.letter.numeric <= 6 && coordinate.rank.number <= 6
        Direction.SE -> coordinate.file.letter.numeric <= 6 && coordinate.rank.number >= 3

        // west
        Direction.SW -> coordinate.file.letter.numeric >= 3 && coordinate.rank.number >= 3
        Direction.NW -> coordinate.file.letter.numeric >= 3 && coordinate.rank.number <= 6
    }


    /**
     * Given a [startJump] coordinate and a [direction], checks if the jump is legal and if the theoretically correct jump
     * middle coordinate is the same as the ones provided as [middleJump] as parameter.
     * */
    private fun areCoordinatesCompatibleForSingleCaptureJump(
        startJump: Coordinate,
        middleJump: Coordinate,
        jumpDirection: Direction
    ) = if (isJumpLegal(startJump, jumpDirection)) {
        val theoreticalMiddleCoordinate = jumpDirection.shiftedCoordinateBy1Diagonally(startJump)

        theoreticalMiddleCoordinate == middleJump
    } else false
}