package com.cioccarellia.checkpigeon.logic.engine.verifier

import com.cioccarellia.checkpigeon.logic.model.move.linear.Move

sealed class VerificationResult {
    data class Passed(val move: Move) : VerificationResult()
    data class Failed(
        val rejectionReason: RejectionReason
    ) : VerificationResult()
}
