import com.cioccarellia.checkpigeon.input.MoveParser
import com.cioccarellia.checkpigeon.input.ParsedMove
import com.cioccarellia.checkpigeon.logic.engine.events.GameEvent
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

object EventParser {

    private const val DUMP = "dump_"
    private const val NOTIFY = "notify_"
    private const val ACTION = "action_"

    fun parse(protoStream: String, currentPlayerColor: TileColor): GameEvent = when {
        protoStream.startsWith(DUMP) -> {
            GameEvent.DumpGame;
        }

        protoStream.startsWith(NOTIFY) -> {
            val moveText = protoStream.removePrefix(NOTIFY)

            when (val parsedMove = MoveParser.convert(moveText, currentPlayerColor)) {
                is ParsedMove.Success -> {
                    GameEvent.SubmissionRequest(parsedMove.move);
                }

                is ParsedMove.Failure -> TODO()
            }
        }

        protoStream.startsWith(ACTION) -> {
            // needs to perform move
            GameEvent.ActionRequest;
        }

        else -> {
            throw IllegalArgumentException()
        }
    }
}