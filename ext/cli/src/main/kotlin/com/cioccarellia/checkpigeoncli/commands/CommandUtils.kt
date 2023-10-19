package com.cioccarellia.checkpigeoncli.commands

fun readCLI(tag: String): String {
    print("$tag > ")
    return readln()
}

fun readCommand(tag: String): ParsedCommand {
    println("$tag > ")
    return CommandParser.convert(readlnOrNull())
}

fun readCommandFromArgs(tag: String, args: Array<String>): ParsedCommand {
    println("$tag > ${args.joinToString(separator = " ")}")
    return CommandParser.convertArgs(args)
}