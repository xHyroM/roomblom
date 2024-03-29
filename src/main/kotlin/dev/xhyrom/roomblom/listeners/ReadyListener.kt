package dev.xhyrom.roomblom.listeners

import dev.xhyrom.roomblom.Bot
import dev.xhyrom.roomblom.managers.CommandManager
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

class ReadyListener : ListenerAdapter() {
    private var registered = false

    override fun onReady(event: ReadyEvent) {
        if (!registered) {
            CommandManager.registerCommandsDiscordAPI(event.jda)
            registered = true
        }

        Bot.getDiscordBotListApi()
            .setStats(event.jda.shardInfo.shardId, event.jda.shardInfo.shardTotal, event.guildTotalCount)

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Bot.getDiscordBotListApi()
                    .setStats(event.jda.shardInfo.shardId, event.jda.shardInfo.shardTotal, event.guildTotalCount)
            }
        }, 0, 600000)
    }
}