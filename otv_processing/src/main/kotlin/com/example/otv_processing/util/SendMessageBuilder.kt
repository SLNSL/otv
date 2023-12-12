package com.example.otv_processing.util

import com.example.otv_processing.dto.UpdateDTO
import com.example.otv_processing.exception.MessageProcessException
import lombok.experimental.UtilityClass
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup

class SendMessageBuilder {
    private var sendMessage: SendMessage = SendMessage()

    companion object {
        fun builder() = SendMessageBuilder()

        fun replyWithExceptionMessage(e: MessageProcessException, updateDTO: UpdateDTO): SendMessage {
            return SendMessageBuilder.builder()
                .message(e.message!!)
                .replyMarkup(ReplyMenuUtil.mainMenu(updateDTO.userTelegramName))
                .build(updateDTO.chatId)
        }
    }

    fun build(chatId: Long): SendMessage {
        this.sendMessage.chatId = chatId.toString()
        return this.sendMessage
    }


    fun message(message: String): SendMessageBuilder {
        this.sendMessage.text = message
        return this
    }

    fun replyMarkup(replyMarkup: ReplyKeyboardMarkup): SendMessageBuilder {
        this.sendMessage.replyMarkup = replyMarkup
        return this
    }
}