package me.sagiri.bot.memory

import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader


suspend fun main(args: Array<String>) {
    MiraiConsoleTerminalLoader.startAsDaemon()
    Main.load()
    Main.enable()

    val bot = MiraiConsole.INSTANCE.addBot(System.getenv("QQ").toLong(), System.getenv("PASSWORD").toString()).alsoLogin()

    MiraiConsole.job.join()
}