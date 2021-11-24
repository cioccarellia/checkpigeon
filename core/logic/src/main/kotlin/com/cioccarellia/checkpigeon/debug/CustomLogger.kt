package com.cioccarellia.checkpigeon.debug

import com.cioccarellia.checkpigeon.constants.LOG_LEVELS

data class CustomLogger(val tag: String, val overriddenLogLevels: List<LogLevel> = LOG_LEVELS)