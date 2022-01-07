import com.cioccarellia.checkpigeon.annotations.ToEngine
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.events.Event
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Usage:
 *
 * ./checkpigeon w 10
 *
 * - first argument determines engine color play
 * - second determines engine depth
 * */

private val executorFlow = MutableSharedFlow<@ToEngine Event>()

private val engine = Engine(
    Player("W", TileColor.WHITE) to Player("B", TileColor.BLACK),
    executorFlow
)


private suspend fun initEngineCollector() {
    engine.engineOutputFlow.collect {
        when (it) {
            is Event.SubmissionProposal.SubmissionAccepted -> {
                print(it.processedMove.humanMoveNotation())
            }
            is Event.SubmissionProposal.SubmissionRejected -> {
                rejectedMove = true
            }
        }
    }
}

private val DEBUG = true
private var rejectedMove = false

suspend fun main(args: Array<String>) {
    val engineColor = when (args[0].lowercase()) {
        "w" -> TileColor.WHITE
        "b" -> TileColor.BLACK
        else -> throw IllegalStateException("Unsupported color parameter ${args[0]}")
    }

    val depth = args[1].toUInt()

    CoroutineScope(Dispatchers.Default).launch {
        initEngineCollector()
    }



    if (DEBUG) {
        engine.stdoutBoard()
    }

    val checkpigeon = CheckPigeon(engine)

    while (engine.status.gameStatus.isAlive) {


        if (engine.status.gameStatus.turnColor == engineColor || rejectedMove) {
            rejectedMove = false

            val engineMove = checkpigeon.extractMove()

            executorFlow.emit(
                Event.SubmissionProposal.SubmissionRequest(
                    submittedMove = engineMove
                )
            )
        }
    }





}