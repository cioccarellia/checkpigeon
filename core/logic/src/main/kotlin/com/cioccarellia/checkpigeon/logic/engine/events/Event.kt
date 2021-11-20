package com.cioccarellia.checkpigeon.logic.engine.events

import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

/**
 * Represents all the events that can be handled by [Engine]
 * */
sealed class Event {

    class Message(val content: String): Event()

    object StartGame : Event()
    object EndGame : Event()

    sealed class SubmissionProposal(): Event() {
        class SubmissionRequest(
            val move: Move
        ) : SubmissionProposal()

        class SubmissionAccepted() : SubmissionProposal()

        class SubmissionRejected(

        ): SubmissionProposal() {



        }
    }


    class Resignation(
        val color: TileColor
    ) : Event()


    sealed class DrawProposal: Event() {

        abstract val color: TileColor

        class DrawRequest(
            override val color: TileColor
        ) : DrawProposal()

        class DrawAccepted(
            override val color: TileColor
        ): DrawProposal()

        class DrawRejected(
            override val color: TileColor
        ): DrawProposal()
    }
}