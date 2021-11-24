package com.cioccarellia.checkpigeon.debug

object DfuLogger {
    private val default = CustomLogger(tag = "Dfu")

    fun d(vararg values: Any?, prefix: String = "D") = default.d(values = values, prefix = prefix)
    fun i(vararg values: Any?, prefix: String = "D") = default.i(values = values, prefix = prefix)
    fun w(vararg values: Any?, prefix: String = "D") = default.w(values = values, prefix = prefix)
    fun e(vararg values: Any?, prefix: String = "D") = default.e(values = values, prefix = prefix)
}