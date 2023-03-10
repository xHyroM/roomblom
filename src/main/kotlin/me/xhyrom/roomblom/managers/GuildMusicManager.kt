package me.xhyrom.roomblom.managers

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import lavalink.client.player.IPlayer
import lavalink.client.player.LavalinkPlayer
import lavalink.client.player.event.PlayerEventListenerAdapter
import me.xhyrom.roomblom.Bot
import net.dv8tion.jda.api.entities.Guild
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class GuildMusicManager(private val guild: Guild) {
    private val link = Bot.getLavaLinkManager().getLavaLink().getLink(guild)
    private val queue = TrackQueue(this)
    private var loop = false

    init {
        link.player.addListener(queue)
    }

    fun getQueue() = queue
    fun getLink() = link
    fun getPlayer(): LavalinkPlayer = link.player

    fun isLoop() = loop
    fun setLoop(loop: Boolean) {
        this.loop = loop
    }

    fun destroy() {
        Bot.getLavaLinkManager().getGuildMusicManagers().remove(guild.idLong)
    }
}

class TrackQueue(private val manager: GuildMusicManager) : PlayerEventListenerAdapter() {
    private val queue: BlockingQueue<AudioTrack> = LinkedBlockingQueue()

    fun add(track: AudioTrack) {
        if (manager.getPlayer().playingTrack == null) {
            manager.getPlayer().playTrack(track)
        } else {
            queue.offer(track)
        }
    }

    fun nextTrack() {
        val track = queue.poll() ?: run {
            manager.getPlayer().stopTrack()

            manager.getPlayer().link.destroy()
            manager.destroy()
            return
        }

        manager.getPlayer().playTrack(track)

        if (manager.isLoop()) queue.offer(track)
    }

    fun getQueue(): BlockingQueue<AudioTrack> {
        return queue
    }

    override fun onTrackEnd(player: IPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        if (endReason == AudioTrackEndReason.FINISHED) {
            nextTrack()
        }
    }
}