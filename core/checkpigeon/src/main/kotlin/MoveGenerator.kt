import MoveGenerator.generateLegalMoves
import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import com.cioccarellia.checkpigeon.output.LegalMovesGenerator

object MoveGenerator {
    fun Board.generateLegalMoves(
         targetColor: TileColor,
         depth: Int
    ): Tree<Move> {
        if (depth > 0) {
            val materialCoordPairs = enumPieces(targetColor)

            materialCoordPairs.forEachIndexed { i, materialCoord ->
                val singleMoves = LegalMovesGenerator.generate(this, materialCoord)

                val virtualBoard = virtualForward()


            }
        } else {
            // Base case: return best move
        }
    }

}

