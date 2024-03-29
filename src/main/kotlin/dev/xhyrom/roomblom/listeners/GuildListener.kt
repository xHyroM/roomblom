package dev.xhyrom.roomblom.listeners

import dev.xhyrom.roomblom.Bot
import dev.xhyrom.roomblom.managers.WebhookSender
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlinx.coroutines.*

class GuildListener : ListenerAdapter() {
    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        if (event.guild.selfMember.voiceState?.channel == null) {
            val musicManager = Bot.getLavaLinkManager().getGuildMusicManagerUnsafe(event.guild)

            musicManager?.getCoroutineScope()?.launch {
                musicManager.getLink().destroy()
                musicManager.destroy()
            }
        }
    }

    override fun onGuildJoin(event: GuildJoinEvent) {
        WebhookSender.sendWebhook(
            Bot.getDotenv().get("WEBHOOK_LOGGER_URL"),
            "New server ||${event.guild.name}|| (${event.guild.id}) <@525316393768452098>"
        )
    }

    override fun onGuildLeave(event: GuildLeaveEvent) {
        WebhookSender.sendWebhook(
            Bot.getDotenv().get("WEBHOOK_LOGGER_URL"),
            "Leave server ||${event.guild.name}|| (${event.guild.id}) <@525316393768452098>"
        )
    }
}