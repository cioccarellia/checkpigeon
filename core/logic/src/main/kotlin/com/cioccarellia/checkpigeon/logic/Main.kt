import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.events.Event
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    val inputFlow = MutableSharedFlow<Event>()

    val game = Engine(
        Player("Apium", TileColor.WHITE) to Player("Titi", TileColor.BLACK),
        inputFlow
    )

    while (true) {
        CoroutineScope(Dispatchers.IO).launch {
            inputFlow.emit(
                Event.Resignation(TileColor.WHITE)
            )
        }
    }
}