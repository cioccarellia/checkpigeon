package com.cioccarellia.checkpigeon.generator

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.board.CardinalDirection
import com.cioccarellia.checkpigeon.logic.console.yellow
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.material.Material
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


fun Coordinate.apply(direction: Direction, color: TileColor, amount: Int = 1): Coordinate? {
    return direction.apply(this, color, amount)
}


enum class Direction {
    FL, FR,
    BL, BR;

    fun apply(base: Coordinate, color: TileColor, amount: Int = 1): Coordinate? {
        try {
            return when (color) {
                TileColor.WHITE -> {
                    when (this) {
                        FL -> base.shifted(-amount, +amount)
                        FR -> base.shifted(+amount, +amount)
                        BL -> base.shifted(-amount, -amount)
                        BR -> base.shifted(+amount, -amount)
                    }
                }
                TileColor.BLACK -> {
                    when (this) {
                        FL -> base.shifted(+amount, -amount)
                        FR -> base.shifted(-amount, -amount)
                        BL -> base.shifted(+amount, +amount)
                        BR -> base.shifted(-amount, +amount)
                    }
                }
            }
        } catch (e: RuntimeException) {
            return null
        }
    }

    fun inverted() = when (this) {
        FL -> BL
        BR -> FR
        FR -> BR
        BL -> FL
    }



    companion object {
        fun full(): Set<Direction> = setOf(FL, FR, BL, BR)
        fun forward(): Set<Direction> = setOf(FL, FR)
        fun backward(): Set<Direction> = setOf(BL, BR)

        fun isForward(direction: Direction) = forward().contains(direction)
        fun isBackward(direction: Direction) = backward().contains(direction)

        fun inferDirection(
            start: Coordinate,
            end: Coordinate
        ): Direction = when {
            end.rawFile() - start.rawFile() > 0 && end.rawRank() - start.rawRank() > 0 -> FR
            end.rawFile() - start.rawFile() > 0 && end.rawRank() - start.rawRank() < 0 -> BR
            end.rawFile() - start.rawFile() < 0 && end.rawRank() - start.rawRank() < 0 -> BL
            end.rawFile() - start.rawFile() < 0 && end.rawRank() - start.rawRank() > 0 -> FL
            else -> throw IllegalArgumentException("Can not determine direction between $start and $end. Are they on a rook line? (0 or infty tangent)")
        }
    }
}


/**
 *
 * */


fun Material.validDirections(playingColor: TileColor): Set<Direction>? = when (this) {
    is Material.Dama -> {
        Direction.forward()
    }

    is Material.Damone -> {
        Direction.full()
    }

    Material.Empty -> {
        null
    }
}.apply {
    when (playingColor) {
        TileColor.WHITE -> {}
        TileColor.BLACK -> {
            this?.forEach { it.inverted() }
        }
    }
}


fun isMovementLegal(board: Board, playingColor: TileColor, coordinate: Coordinate, direction: Direction): Boolean {
    // first we get the new coordinate, and check whether it's out of bounds
    val shiftedCoordinate: Coordinate? = coordinate.apply(direction, playingColor, amount = 1)

    return if (shiftedCoordinate == null) {
        // out of bounds
        false;
    } else {
        when (val mat = board[shiftedCoordinate]) {
            Material.Empty -> true
            else -> false
        }
    }
}



private fun can_attack(attacker: Material, defendant: Material): Boolean = when (attacker) {
    is Material.Dama -> true
    is Material.Damone -> when (defendant) {
        is Material.Dama -> true
        is Material.Damone -> false
        Material.Empty -> throw IllegalArgumentException()
    }
    Material.Empty -> {
        // TODO Warn
        // println("warn: space tries to move".yellow())
        false
    }
}


fun canCaptureAndJumpOver(board: Board, playingColor: TileColor, startCoordinate: Coordinate, direction: Direction): Boolean {
    // first we get the new coordinate, and check whether it's out of bounds
    val shiftedCoordinateOnce: Coordinate? = startCoordinate.apply(direction, playingColor, amount = 1)     // defendant
    val shiftedCoordinateTwice: Coordinate? = startCoordinate.apply(direction, playingColor, amount = 2)    // landing zone

    return if (shiftedCoordinateOnce == null || shiftedCoordinateTwice == null) {
        // either is out of bounds
        false;
    } else {
        val material = board[startCoordinate]               // starting material
        val defendant = board[shiftedCoordinateOnce]        // defending material (should be weaker or equal)
        val landing = board[shiftedCoordinateTwice]         // landing zone material (should be empty)


        if (material.color() != null) {
            assert(playingColor == material.color())
        }

        if (material is Material.Empty) {
            // println("warn: space tries to move! Capture with [$material] from ${startCoordinate}x${shiftedCoordinateOnce}, capturing [$defendant] and landing on $shiftedCoordinateTwice, which contains $landing".yellow())
            // println("------------------------------------- printing incriminated board".yellow())
            // board.print(playingColor, listOf(startCoordinate))
            // println("------------------------------------- printed board".yellow())


            return false
        }

        if (landing !is Material.Empty) {
            // trying to land on something
            return false
        }

        if (defendant is Material.Empty) {
            // trying to capture nothing
            return false
        }

        if (defendant.color() != null && defendant.color() == playingColor) {
            // trying to capture your own piece
            return false
        }

        if (!can_attack(material, defendant)) {
            // trying to attack a stronger enemy
            return false
        }

        return true
    }
}


