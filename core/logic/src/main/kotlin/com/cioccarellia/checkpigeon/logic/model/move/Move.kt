package com.cioccarellia.checkpigeon.logic.model.move

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

data class Move(
    override val moveType: MoveType,
    override val playingColor: TileColor,

    override val start: Coordinate,
    override val end: Coordinate,

    override val captures: List<Coordinate>,
    override val blows: Pair<Coordinate, Coordinate>,
) : MoveUnit() {
    /**
     * Generates movetext for the given move
     * */
    fun humanMoveNotation(): String {
        val blowText = "" // TODO

        val captureText =
            captures.mapIndexed { index, it -> it.toString() + if (index == captures.size) "" else "x" }.toString()

        return buildString {
            append(blowText)

            append(
                when (moveType) {
                    // marks start and end explicitly
                    is Movement -> "${start}-${end}"

                    // end coordinates are inferred
                    is Capture -> "${start}x${captureText}"
                }
            )
        }
    }


    /**
     * Generates draught notation for the given move
     * */
    fun draughtNotation(): String =
        start.checkersCoordinateNumber.toString() + moveType.symbol + end.checkersCoordinateNumber.toString()
}