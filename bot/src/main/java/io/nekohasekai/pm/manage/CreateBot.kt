package io.nekohasekai.pm.manage

import cn.hutool.core.util.NumberUtil
import com.pengrad.telegrambot.request.GetMe
import io.nekohasekai.ktlib.core.input
import io.nekohasekai.ktlib.td.core.*
import io.nekohasekai.ktlib.td.core.extensions.*
import io.nekohasekai.ktlib.td.core.utils.*
import io.nekohasekai.ktlib.td.http.httpSync
import io.nekohasekai.ktlib.td.i18n.*
import io.nekohasekai.pm.*
import io.nekohasekai.pm.database.UserBot
import io.nekohasekai.pm.instance.BotInstances
import td.TdApi

class CreateBot : TdHandler() {

    companion object {

        const val command = "new_bot"

        const val persistId = PERSIST_BOT_CREATE
    }

    fun def() = TdApi.BotCommand(
            command,
            clientLocale.CREATE_BOT_DEF
    )

    override fun onLoad() {

        initFunction(command)
        initPersist(persistId)

    }

    override suspend fun onFunction(userId: Int, chatId: Long, message: TdApi.Message, function: String, param: String, params: Array<String>, originParams: Array<String>) {

        if (!message.fromPrivate) {

            sudo makeHtml localeFor(userId).FN_PRIVATE_ONLY onSuccess deleteDelay(message) replyTo message

            return

        }

        if (chatId != Launcher.admin) {

            if (!Launcher.public) rejectFunction()

            if (!Launcher.userAccessible(userId)) {

                sudo makeMd localeFor(userId).PRIVATE_INSTANCE.input(Launcher.repoUrl) syncTo chatId

                return

            }

        }

        if (param.isTokenInvalid) {

            userCalled(userId, "start create with non-valid token param")

            startCreate(userId, chatId)

        } else {

            userCalled(userId, "create with valid token: $param")

            createByToken(userId, chatId, param)

        }

    }

    fun startCreate(userId: Int, chatId: Long) {

        val L = localeFor(userId)

        sudo makeHtml L.INPUT_BOT_TOKEN sendTo chatId

        writePersist(userId, persistId, 0L)

    }

    override suspend fun onPersistMessage(userId: Int, chatId: Long, message: TdApi.Message, subId: Long, data: Array<Any?>) {

        userCalled(userId, "inputted token: ${message.text}")

        createByToken(userId, chatId, message.text)

    }

    val String?.isTokenInvalid get() = this == null || length < 40 || length > 50 || !contains(":") || !NumberUtil.isInteger(substringBefore(":"))

    suspend fun createByToken(userId: Int, chatId: Long, token: String?) {

        val L = localeFor(userId)

        if (token.isTokenInvalid) {

            userCalled(userId, "token invalid")

            sudo make L.INVALID_BOT_TOKEN.input("") sendTo chatId

            return

        }

        removePersist(userId)

        val status = sudo make L.FETCHING_INFO syncTo chatId

        sudo make Typing sendTo chatId

        val botMe = try {

            httpSync(token!!, GetMe()).user()

        } catch (e: TdException) {

            userCalled(userId, "token invalid: ${e.message}")

            sudo make L.INVALID_BOT_TOKEN.input(" (${e.message})") editTo status

            return

        }

        val exists = TdClient.clients.any { botMe.id() == it.me.id } || database { UserBot.findById(botMe.id()) } != null

        if (exists) {

            userCalled(userId, "created bot but exists: ${botMe.username()} (${botMe.id()})")

            sudo make L.failed { ALREADY_EXISTS } syncEditTo status

            sudo make CancelChatAction syncTo chatId

            return

        }

        sudo make Typing sendTo chatId

        sudo make L.CREATING_BOT editTo status

        val userBot = database.write {

            UserBot.new(botMe.id()) {

                botToken = token
                username = botMe.username()
                owner = userId

            }

        }

        if (BotInstances.initBot(userBot).waitForAuth()) {

            sudo makeHtml L.FINISH_CREATION.input(userBot.username) editTo status

        } else {

            warnUserCalled(userId, "start failed when created")

        }


    }

}