package com.cioccarellia.checkpigeon.logic.engine.verifier

import com.cioccarellia.checkpigeon.logic.board.*
import com.cioccarellia.checkpigeon.logic.engine.status.GameStatus
import com.cioccarellia.checkpigeon.logic.engine.verifier.VerificationResult.Failed
import com.cioccarellia.checkpigeon.logic.engine.verifier.VerificationResult.Passed
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

object MoveVerifier {

    fun Move.toBlowMove(): Move? {
        check(blows != null)

        return try {
            Move(
                moveType = MoveType.Capture,
                playingColor = !playingColor,
                start = blows.first,
                end = CardinalDirection.infer(start, blows.second).shiftedCoordinateBy2Diagonally(start),
                captures = listOf(blows.second),
                blows = null
            )
        } catch (iae: IllegalArgumentException) {
            null
        }
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
        * Move-dependent checks (without blow checks)
        * */
        val partialVerification = when (move.moveType) {
            MoveType.Movement -> verifyMovement(move, board, status)
            MoveType.Capture -> verifyCapture(move, board, status)
        }

        if (partialVerification is Failed || move.blows == null) {
            return partialVerification
        } else {
            // Result is passed, and we have to check against blow errors.
            val mockStatus = status.apply {
                onMoveAccepted(move)
            }

            val mockBlowMove = move.toBlowMove()

            return if (mockBlowMove == null) {
                Failed(RejectionReason.BLOW_MOVE_DATA_INCOHERENCY)
            } else {
                when (verifyCapture(mockBlowMove, board, mockStatus)) {
                    is Failed -> Failed(RejectionReason.BLOW_DISALLOWED_CAPTURE)
                    is Passed -> partialVerification
                }
            }
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
                    val forwardLeftCheck = areCoordinatesCompatibleForMovement(move.start, move.end, CardinalDirection.NW)
                    val forwardRightCheck = areCoordinatesCompatibleForMovement(move.start, move.end, CardinalDirection.NE)

                    if (!forwardLeftCheck && !forwardRightCheck) {
                        // in case none of them is valid
                        return Failed(RejectionReason.MOVEMENT_DISALLOWED_MOVEMENT)
                    }
                } else {
                    // With black, forward = south
                    val forwardLeftCheck = areCoordinatesCompatibleForMovement(move.start, move.end, CardinalDirection.SW)
                    val forwardRightCheck = areCoordinatesCompatibleForMovement(move.start, move.end, CardinalDirection.SE)

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
                val forwardLeftCheck = areCoordinatesCompatibleForMovement(move.start, move.end, CardinalDirection.NW)
                val forwardRightCheck = areCoordinatesCompatibleForMovement(move.start, move.end, CardinalDirection.NE)
                val backwardLeftCheck = areCoordinatesCompatibleForMovement(move.start, move.end, CardinalDirection.SW)
                val backwardRightCheck = areCoordinatesCompatibleForMovement(move.start, move.end, CardinalDirection.SE)

                when {
                    forwardLeftCheck -> {
                        // good
                    }

                    forwardRightCheck -> {
                        // good
                    }

                    backwardLeftCheck -> {
                        // good
                    }

                    backwardRightCheck -> {
                        // good
                    }

                    else -> {
                        // fuck you
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
                                    jumpDirection = CardinalDirection.NW
                                )

                                val forwardRightJumpCheck = areCoordinatesCompatibleForSingleCaptureJump(
                                    startJump = startJumpCoordinate,
                                    middleJump = currentCaptureCoords,
                                    jumpDirection = CardinalDirection.NE
                                )

                                when {
                                    forwardLeftJumpCheck -> {
                                        val jumpDestination =
                                            CardinalDirection.NW.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
                                        lastSuccessfulJumpLand = jumpDestination
                                    }

                                    forwardRightJumpCheck -> {
                                        val jumpDestination =
                                            CardinalDirection.NE.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
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
                                    jumpDirection = CardinalDirection.SE
                                )

                                val forwardRightJumpCheck = areCoordinatesCompatibleForSingleCaptureJump(
                                    startJump = startJumpCoordinate,
                                    middleJump = currentCaptureCoords,
                                    jumpDirection = CardinalDirection.SW
                                )

                                when {
                                    forwardLeftJumpCheck -> {
                                        val jumpDestination =
                                            CardinalDirection.SE.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
                                        lastSuccessfulJumpLand = jumpDestination
                                    }

                                    forwardRightJumpCheck -> {
                                        val jumpDestination =
                                            CardinalDirection.SW.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
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
                            jumpDirection = CardinalDirection.NW
                        )

                        val forwardRightJumpCheck = areCoordinatesCompatibleForSingleCaptureJump(
                            startJump = startJumpCoordinate,
                            middleJump = currentCaptureCoords,
                            jumpDirection = CardinalDirection.NE
                        )

                        val backwardRightJumpCheck = areCoordinatesCompatibleForSingleCaptureJump(
                            startJump = startJumpCoordinate,
                            middleJump = currentCaptureCoords,
                            jumpDirection = CardinalDirection.SE
                        )

                        val backwardLeftJumpCheck = areCoordinatesCompatibleForSingleCaptureJump(
                            startJump = startJumpCoordinate,
                            middleJump = currentCaptureCoords,
                            jumpDirection = CardinalDirection.SW
                        )


                        when {
                            forwardLeftJumpCheck -> {
                                val jumpDestination =
                                    CardinalDirection.NW.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
                                lastSuccessfulJumpLand = jumpDestination
                            }

                            forwardRightJumpCheck -> {
                                val jumpDestination =
                                    CardinalDirection.NE.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
                                lastSuccessfulJumpLand = jumpDestination
                            }

                            backwardLeftJumpCheck -> {
                                val jumpDestination =
                                    CardinalDirection.SW.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
                                lastSuccessfulJumpLand = jumpDestination
                            }

                            backwardRightJumpCheck -> {
                                val jumpDestination =
                                    CardinalDirection.SE.shiftedCoordinateBy2Diagonally(startJumpCoordinate)
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
}