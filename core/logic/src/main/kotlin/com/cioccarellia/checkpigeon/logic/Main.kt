package com.cioccarellia.checkpigeon.logic

import com.cioccarellia.checkpigeon.logic.console.*
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.events.Event
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect

val inputFlow = MutableSharedFlow<Event>()

val engine = Engine(
    Player("Apium", TileColor.WHITE) to Player("Titi", TileColor.BLACK),
    inputFlow
)

fun main(args: Array<String>): Unit = runBlocking {
    startGame()
    /**
     * Async Dispatcher reading flow, never terminating
     * */
    CoroutineScope(Dispatchers.Default).launch {
        println("Initializing".yellowBackground())
        engine.engineOutputFlow.collect {
            println("Received event from Engine".yellow())
            when (it) {
                is Event.Message -> {
                    println("Received Message \"${it.content}\"".red())
                }
                else -> {
                    println("UELA".yellowBackground())
                }
            }
        }
    }

    inputFlow.emit(
        Event.Message("Sup")
    )



    while (true) {
        delay(200)
        println(
            bracket("Input".green()) + "Input character:"
        )

        inputFlow.emit(
            Event.Message(readLine()!!)
        )

    }
}


suspend fun startGame() {
    inputFlow.emit(
        Event.StartGame
    )
}