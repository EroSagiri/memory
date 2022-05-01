package me.sagiri.bot.memory

import me.sagiri.bot.memory.data.FlashImageMesageChain
import me.sagiri.bot.memory.data.RecallMesageChain
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.*

object Main : KotlinPlugin(
    JvmPluginDescription(
        id = "me.sagiri.bot.memory",
        name = "memory",
        version = "1.1.1"
    ) {
        author("sagiri")

        info(
            """
                old memory
            """.trimIndent()
        )
    }
) {
    override fun onEnable() {
        val flashImageMessageChain = mutableSetOf<FlashImageMesageChain>()
        val messages = mutableSetOf<MessageChain>()
        val recallMessages = mutableSetOf<RecallMesageChain>()

        /**
         * 群撤回事件
         */
        globalEventChannel().subscribeAlways<MessageRecallEvent.GroupRecall> { event ->
            messages.forEach {
                if(it.ids.contentEquals(event.messageIds)) {
                    recallMessages.add(
                        RecallMesageChain(
                            groupId = event.group.id,
                            senderId = event.authorId,
                            message = it,
                            time = event.messageTime
                        )
                    )
                    logger.info("${event.author.nick} 撤回 ${it}")
                }
            }
        }

        /**
         * 群消息事件
         */
        globalEventChannel().subscribeAlways<GroupMessageEvent> { event ->
            if (message.any { it is FlashImage }) {
                val tempMessage = MessageChainBuilder()
                message.forEach { singleMessage ->
                    if (singleMessage is FlashImage) {
                        tempMessage.add(singleMessage.image)
                    } else {
                        tempMessage.add(singleMessage)
                    }
                }

                flashImageMessageChain.add(
                    FlashImageMesageChain(
                        groupId = event.group.id,
                        senderId = event.sender.id,
                        message = tempMessage.toMessageChain(),
                        time = event.time
                    )
                )

                logger.info("${event.group.name} ${event.sender.nick} 在发闪图")
            }

            if (message.any { it is At } && "刚刚说了什么" in message.content) {
                message.forEach { singleMessage ->
                    if (singleMessage is At) {
                        val friend = bot.getFriend(event.sender.id)
                        friend.let {
                            flashImageMessageChain.forEach { flashImageMesageChain ->
                                if (flashImageMesageChain.senderId == singleMessage.target && flashImageMesageChain.groupId == event.group.id) {
                                    friend?.sendMessage("${singleMessage.target}")
                                    friend?.sendMessage(flashImageMesageChain.message)
                                }
                            }

                            recallMessages.forEach {
                                if(it.groupId == event.group.id && it.senderId == singleMessage.target) {
                                    friend?.sendMessage(it.message)
                                }
                            }
                        }
                    }
                }
            }

            // 保存消息
            messages.add(event.message)

            // 超过两分钟删除
            messages.forEach {
                if(event.time - it.time > 120) {
                    messages.remove(it)
                }
            }
        }
    }

    override fun onDisable() {

    }
}