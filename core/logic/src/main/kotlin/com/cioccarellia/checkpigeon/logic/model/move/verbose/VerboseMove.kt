package com.cioccarellia.checkpigeon.logic.model.move.verbose

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.MoveUnit
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

/**
 * The base [Move] class does not need to explicitly contain elements
 * such as the captured/blown material, because it is information which
 * can be destroyed without impact on the game, given it follows a linear walkthrough.
 *
 * This class contains additional
 * */
data class VerboseMove(
    override val moveType: MoveType,
    override val playingColor: TileColor,

    override val start: Coordinate,
    val startMaterial: Material,

    override val end: Coordinate,
    val endMaterial: Material,

    override val captures: List<Coordinate>,
    val capturedMaterial: List<Material>,

    override val blows: Pair<Coordinate, Coordinate>?,
    val blownMaterial: Material?,
) : MoveUnit() {

    init {
        check(captures.size == capturedMaterial.size)
        check((blows != null && blownMaterial != null) || (blows == null && blownMaterial == null))
    }

    override val promotion: Coordinate? = null

    fun captureCoordsMaterialPairList() = captures.zip(capturedMaterial)
}