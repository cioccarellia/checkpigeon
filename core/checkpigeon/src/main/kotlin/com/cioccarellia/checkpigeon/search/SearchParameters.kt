package com.cioccarellia.checkpigeon.search

import java.security.SecureRandom
import java.util.Random

object SearchParameters {
    const val USE_EVAL = true

    const val MAX = +1_000_000
    const val MIN = -1_000_000

    const val MAX_DEPTH = 5
    const val RANDOMIZED = false

    val seed: () -> Random = {
        SecureRandom()
    }

    object Debug {
        const val DEBUG_ALL = false
        const val DEBUG_EVAL = true
    }
}