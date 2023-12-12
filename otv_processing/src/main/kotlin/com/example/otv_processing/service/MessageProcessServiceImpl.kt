package com.example.otv_processing.service

import com.example.otv_processing.dto.UpdateDTO
import com.example.otv_processing.entity.Student
import com.example.otv_processing.exception.MessageProcessException
import com.example.otv_processing.repository.GroupRepository
import com.example.otv_processing.repository.StudentRepository
import com.example.otv_processing.service.abstraction.MessageProcessService
import com.example.otv_processing.service.handler.CommandHandler
import com.example.otv_processing.service.handler.TextHandler
import com.example.otv_processing.util.MessageResolveUtil.Companion.isGroupCommand
import com.example.otv_processing.util.MessageResolveUtil.Companion.isNotificationsCommand
import com.example.otv_processing.util.MessageResolveUtil.Companion.isPeriodCommand
import com.example.otv_processing.util.MessageResolveUtil.Companion.isStartCommand
import com.example.otv_processing.util.SendMessageBuilder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class MessageProcessServiceImpl(
    private val studentRepository: StudentRepository,
    private val groupRepository: GroupRepository,
    private val containedProcessor: TextHandler,
    private val commandProcessor: CommandHandler
) : MessageProcessService {

    @Transactional
    override fun process(update: Update): SendMessage {
        val updateDTO = UpdateDTO(
            message = update.message.text,
            userTelegramName = update.message.from.userName,
            chatId = update.message.chatId
        )

        val sendMessage = try {
            resolveAndProcess(updateDTO)
        } catch (e: MessageProcessException) {
            SendMessageBuilder.replyWithExceptionMessage(e, updateDTO.chatId)
        }

        return sendMessage
    }

    private fun resolveAndProcess(updateDTO: UpdateDTO): SendMessage {
        var student = Student()

        if (!isStartCommand(updateDTO.message)) {
            student =
                studentRepository.findByTelegramName(updateDTO.userTelegramName)
                    ?: throw MessageProcessException("${updateDTO.userTelegramName} не найден в базе! Введи /start.")
        }

        var needUpdateLastCommand = true

        val sendMessage = when {
            isStartCommand(updateDTO.message) -> commandProcessor.processStartCommand(updateDTO)
            isGroupCommand(updateDTO.message) -> commandProcessor.processGroupCommand(updateDTO, student)
            isPeriodCommand(updateDTO.message) -> commandProcessor.processPeriodCommand(updateDTO, student)
            isNotificationsCommand(updateDTO.message) -> commandProcessor.processNotificationCommand(updateDTO, student)

            else -> {
                needUpdateLastCommand = false
                containedProcessor.processNotCommand(updateDTO, student)
            }
        }

        if (needUpdateLastCommand) {
            studentRepository.updateLastCommand(updateDTO.userTelegramName, updateDTO.message)
        }

        return sendMessage
    }
}