package me.xhyrom.roomblom

import io.github.cdimascio.dotenv.Dotenv
import me.xhyrom.roomblom.listeners.GuildListener
import me.xhyrom.roomblom.listeners.InteractionListener
import me.xhyrom.roomblom.listeners.ReadyListener
import me.xhyrom.roomblom.managers.CommandManager
import me.xhyrom.roomblom.managers.LavalinkManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import org.discordbots.api.client.DiscordBotListAPI
import redis.clients.jedis.JedisPooled

object Bot {
    const val MASCOT = "<:roomblom:1060621623188787340>"

    private var dotenv: Dotenv = Dotenv.load()
    private var shardManager: ShardManager? = null
    private var discordBotListApi: DiscordBotListAPI? = null
    private var lavaLinkManager: LavalinkManager? = null
    private var redis: JedisPooled? = null

    @JvmStatic
    fun main(args: Array<String>) {
        lavaLinkManager = LavalinkManager()

        shardManager = DefaultShardManagerBuilder.createDefault(dotenv.get("BOT_TOKEN"))
            .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
            .addEventListeners(InteractionListener())
            .addEventListeners(GuildListener())
            .addEventListeners(ReadyListener())
            .setVoiceDispatchInterceptor(lavaLinkManager!!.getLavaLink().voiceInterceptor)
            .build()

        discordBotListApi = DiscordBotListAPI.Builder()
            .token(dotenv.get("TOPGG_TOKEN"))
            .botId(dotenv.get("BOT_CLIENT_ID"))
            .build()

        redis = JedisPooled(dotenv.get("REDIS_HOST"), dotenv.get("REDIS_PORT").toInt(), dotenv.get("REDIS_USERNAME"), dotenv.get("REDIS_PASSWORD"))

        CommandManager.registerCommands()
    }

    fun getDotenv(): Dotenv {
        return dotenv
    }

    fun getShardManager(): ShardManager {
        return shardManager!!
    }

    fun getDiscordBotListApi(): DiscordBotListAPI {
        return discordBotListApi!!
    }

    fun getLavaLinkManager(): LavalinkManager {
        return lavaLinkManager!!
    }

    fun getRedis(): JedisPooled {
        return redis!!
    }
}