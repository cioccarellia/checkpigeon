package com.cioccarellia.checkpigeon.logic.console

object Tinter {
    private const val ESCAPE = '\u001B'
    private const val RESET = "$ESCAPE[0m"

    /**
     * Create a string that will be printed with the specified color as foreground
     * @param string The string to color
     * @param color The color to use
     * @return The colored string
     */
    fun f(string: String, color: Color) = tint(string, color.foreground)

    /**
     * Create a string that will be printed with the specified color as background
     * @param string The string to color
     * @param color The color to use
     * @return The colored string
     */
    fun b(string: String, color: Color) = tint(string, color.background)

    private fun tint(string: String, ansiString: String) = "$ansiString$string$RESET"
}