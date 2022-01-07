import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move

class CheckPigeon(
    val engine: Engine
) {
    var working = false

    suspend fun extractMove(): Move {

    }
}