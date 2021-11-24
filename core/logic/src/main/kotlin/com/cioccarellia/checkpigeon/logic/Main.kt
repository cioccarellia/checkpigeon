package com.cioccarellia.checkpigeon.logic

import com.cioccarellia.checkpigeon.logic.console.*
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.events.Event
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect

val mainFlow = MutableSharedFlow<Event>()

val engine = Engine(
    Player("Apium", TileColor.WHITE) to Player("Titi", TileColor.BLACK),
    mainFlow
)

fun main(args: Array<String>): Unit = runBlocking {
    mainFlow.emit(
        Event.StartGame
    )

    /**
     * Async Dispatcher reading flow, never terminating
     * */
    CoroutineScope(Dispatchers.Default).launch {
        println("Initializing Main->Engine collector".yellowBackground())
        engine.engineOutputFlow.collect {
            println("Received event from Engine".yellow())

            when (it) {
                is Event.Message -> {
                    println("Received Message \"${it.content}\"".red())
                }
                else -> {

                }
            }
        }
    }

    mainFlow.emit(
        Event.Message("Sup")
    )

    while (true) {
        delay(200)
        println(
            "Input character:"
        )

        mainFlow.emit(
            Event.Message(readLine()!!)
        )
    }
}
