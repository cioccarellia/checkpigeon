import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.move.verbose.VerboseMove
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

fun Board.enumPieces(targetColor: TileColor): List<Pair<Material, Coordinate>> = matrix.map {
    it.mapNotNull{ tile ->
        when (val material = tile.material) {
            is Material.Dama -> if (material.color == targetColor) material to tile.coordinate else null
            is Material.Damone -> if (material.color == targetColor) material to tile.coordinate else null
            else -> null
        }
    }
}.flatten()


fun Board.virtualForward(validatedMove: Move): Board = copy().apply {
    executeMoveForward(validatedMove)
}

fun Board.virtualBackward(validatedMove: VerboseMove): Board = copy().apply {
    executeMoveBackward(validatedMove)
}