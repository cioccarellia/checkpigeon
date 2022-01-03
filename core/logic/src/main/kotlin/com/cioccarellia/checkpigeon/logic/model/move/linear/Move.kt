package com.cioccarellia.checkpigeon.logic.model.move.linear

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.MoveUnit
import com.cioccarellia.checkpigeon.logic.model.move.symbol
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

data class Move(
    override val moveType: MoveType,
    override val playingColor: TileColor,

    override val start: Coordinate,
    override val end: Coordinate,

    override val captures: List<Coordinate>,
    override val blows: Pair<Coordinate, Coordinate>?,
) : MoveUnit() {

    override var promotion: Coordinate? = null

    /**
     * Generates movetext for the given move
     * */
    fun humanMoveNotation(): String {
        val blowText = if (blows == null) {
            ""
        } else "${blows.first}x${blows.second}"

        val captureText = captures
            .mapIndexed { index, captureCoordinate ->
                captureCoordinate.toString() + if (index == captures.lastIndex) "" else "x"
            }
            .toString()

        return buildString {
            append(blowText)

            append(
                when (moveType) {
                    // marks start and end explicitly
                    is MoveType.Movement -> "${start}-${end}"

                    // end coordinates are inferred
                    is MoveType.Capture -> "${start}x${captureText}"
                }
            )
        }
    }

    /**
     * Generates draught notation for the given move
     * */
    fun draughtNotation(): String =
        start.checkersCoordinateNumber.toString() + moveType.symbol() + end.checkersCoordinateNumber.toString()
}