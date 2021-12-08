package com.cioccarellia.checkpigeon.logic.engine.events

import com.cioccarellia.checkpigeon.annotations.FromEngine
import com.cioccarellia.checkpigeon.annotations.ToEngine
import com.cioccarellia.checkpigeon.logic.engine.verifier.RejectionDetails
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

/**
 * Represents all the events that can be handled by [Engine]
 * */
@FromEngine
@ToEngine
sealed class Event {

    @FromEngine
    @ToEngine
    data class Message(val content: String) : Event()

    @ToEngine
    object StartGame : Event()

    @ToEngine
    object EndGame : Event()

    @FromEngine
    @ToEngine
    sealed class SubmissionProposal : Event() {
        @ToEngine
        data class SubmissionRequest(
            // unsafe move to execute, has to be checked
            val submittedMove: Move
        ) : SubmissionProposal()

        @FromEngine
        data class SubmissionAccepted(
            val processedMove: Move,
            val message: String? = null
        ) : SubmissionProposal()

        @FromEngine
        data class SubmissionRejected(
            val rejectionDetails: RejectionDetails,
            val message: String? = null
        ) : SubmissionProposal()
    }


    @FromEngine
    @ToEngine
    data class Resignation(
        val color: TileColor
    ) : Event()


    @FromEngine
    @ToEngine
    sealed class DrawProposal : Event() {

        abstract val color: TileColor

        @FromEngine
        @ToEngine
        data class DrawRequest(
            override val color: TileColor
        ) : DrawProposal()

        @FromEngine
        @ToEngine
        data class DrawAccepted(
            override val color: TileColor
        ) : DrawProposal()

        @FromEngine
        @ToEngine
        data class DrawRejected(
            override val color: TileColor
        ) : DrawProposal()
    }
}