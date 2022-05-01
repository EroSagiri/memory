package me.sagiri.bot.memory.data

import net.mamoe.mirai.message.data.MessageChain

data class FlashImageMesageChain(
    val groupId : Long,
    val senderId : Long,
    val message : MessageChain,
    val time : Int
)

data class RecallMesageChain(
    val groupId : Long,
    val senderId : Long,
    val message : MessageChain,
    val time : Int
)