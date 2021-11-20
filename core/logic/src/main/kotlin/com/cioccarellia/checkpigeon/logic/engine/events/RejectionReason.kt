package com.cioccarellia.checkpigeon.logic.engine.events

sealed class RejectionReason {
    sealed class SyntaxError : RejectionReason() {
        object MissingFields : SyntaxError()
        object MalformedFields : SyntaxError()
    }

    sealed class SemanticError : RejectionReason() {

    }
}