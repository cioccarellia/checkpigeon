package com.cioccarellia.checkpigeoncli.utils

object CLIUtil {

    fun welcomeScreen() = println(AsciiBanners.bannerList.random().toRandomColor())

    fun helpPage() = println(
        """
            CheckPigeon CLI
            usage: [command] [parameters]
            
            COMMANDS AND PARAMETERS:
            - help
            - exit
            - import source=[stdin|file] [input] [filepath]
            - export destination=[stdout|file]
            - play mode=[hh|he|ee] black=A white=B
            
        """.trimIndent()
    )
}