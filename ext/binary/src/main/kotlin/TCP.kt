import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.events.GameEvent
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

fun main(args: Array<String>): Unit = runBlocking {

    // parse args
    val SERVER_HOST = args[0];
    val SERVER_PORT = args[1].toInt();
    val isCurrentPlayerWhite = args[2].toBoolean();


    // create engine
    val engine = Engine(
        if (isCurrentPlayerWhite) {
            Player("Self", TileColor.WHITE, true) to Player("Opponent", TileColor.BLACK, false)
        } else {
            Player("Opponent", TileColor.WHITE, false) to Player("Self", TileColor.BLACK, true)
        }
    )


    // tune in to socket
    val socket = Socket(SERVER_HOST, SERVER_PORT)
    val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    val writer = PrintWriter(socket.getOutputStream(), true)


    // loop for messages
    while (true) { // engine.status.gameStatus.isAlive
        println("Waiting for messages")
        val receivedMessage = reader.readLine()

        println("Received message: $receivedMessage")

        val event: GameEvent = TODO(); // parse move

        when (event) {
            GameEvent.DumpGame -> {

                val turnColor = engine.status.gameStatus.turnColor
                val isAlive = engine.status.gameStatus.isAlive

                val gameMatrix = engine.expose().matrix

                // write game dump out
                writer.write("...");
            }
            is GameEvent.SubmissionRequest -> {
                // need to apply the given move
                engine.applyMove(event.submittedMove)

                // write ack and move on

                writer.write("ack");
            }

            GameEvent.ActionRequest -> {
                // need to produce a move

                // and write it out
            }
        }
    }
}