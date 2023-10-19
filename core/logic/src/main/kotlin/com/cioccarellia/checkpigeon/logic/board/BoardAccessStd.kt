package com.cioccarellia.checkpigeon.logic.board

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor


fun isPromotionSquare(
    color: TileColor,
    square: Coordinate
) = when (color) {
    TileColor.WHITE -> square.rank.number == 8
    TileColor.BLACK -> square.rank.number == 1
}


/**
 * Returns whether a movement is allowed for a certain coordinate, in order
 * not to fall of the board.
 * */
fun isMovementLegal(coordinate: Coordinate, direction: CardinalDirection) = when (direction) {
    // east
    CardinalDirection.NE -> coordinate.file.letter.numeric <= 7 && coordinate.rank.number <= 7
    CardinalDirection.SE -> coordinate.file.letter.numeric <= 7 && coordinate.rank.number >= 2

    // west
    CardinalDirection.SW -> coordinate.file.letter.numeric >= 2 && coordinate.rank.number >= 2
    CardinalDirection.NW -> coordinate.file.letter.numeric >= 2 && coordinate.rank.number <= 7
}

/**
 * Returns whether a 1-diagonal movement from [start] to [end] through the [direction] is legal and matches the given direction.
 * */
fun areCoordinatesCompatibleForMovement(start: Coordinate, end: Coordinate, direction: CardinalDirection) =
    if (isMovementLegal(start, direction)) {
        val theoreticalCoordinate = direction.shiftedCoordinateBy1Diagonally(start)

        end == theoreticalCoordinate
    } else false


fun isJumpLegal(coordinate: Coordinate, direction: CardinalDirection) = when (direction) {
    // east
    CardinalDirection.NE -> coordinate.file.letter.numeric <= 6 && coordinate.rank.number <= 6
    CardinalDirection.SE -> coordinate.file.letter.numeric <= 6 && coordinate.rank.number >= 3

    // west
    CardinalDirection.SW -> coordinate.file.letter.numeric >= 3 && coordinate.rank.number >= 3
    CardinalDirection.NW -> coordinate.file.letter.numeric >= 3 && coordinate.rank.number <= 6
}


/**
 * Given a [startJump] coordinate and a [direction], checks if the jump is legal and if the theoretically correct jump
 * middle coordinate is the same as the ones provided as [middleJump] as parameter.
 * */
fun areCoordinatesCompatibleForSingleCaptureJump(
    startJump: Coordinate,
    middleJump: Coordinate,
    jumpDirection: CardinalDirection
) = if (isJumpLegal(startJump, jumpDirection)) {
    val theoreticalMiddleCoordinate = jumpDirection.shiftedCoordinateBy1Diagonally(startJump)

    theoreticalMiddleCoordinate == middleJump
} else false