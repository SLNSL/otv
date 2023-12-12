package com.example.otv_processing.service.handler

import com.example.otv_processing.dto.UpdateDTO
import com.example.otv_processing.entity.Student
import com.example.otv_processing.messaging.QueueSender
import com.example.otv_processing.repository.GroupRepository
import com.example.otv_processing.repository.PeriodRepository
import com.example.otv_processing.repository.StudentRepository
import com.example.otv_processing.util.ReplyMenuUtil
import com.example.otv_processing.util.SendMessageBuilder
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Component
class CommandHandler(
    private val studentRepository: StudentRepository,
    private val groupRepository: GroupRepository,
    private val periodRepository: PeriodRepository,

    private val containedProcessor: TextHandler
) {
    private val queueSender = QueueSender()

    fun processStartCommand(updateDTO: UpdateDTO): SendMessage {
        var student = studentRepository.findByTelegramName(updateDTO.userTelegramName)
        if (student != null) {
            return containedProcessor.processTrash(
                updateDTO, student,
                "Ты уже зарегистрирован, ${updateDTO.userTelegramName}!"
            )
        }

        student = Student()
        student.telegramName = updateDTO.userTelegramName
        student.telegramChatId = updateDTO.chatId
        student.isNotificationEnabled = false
        studentRepository.save(student)

        val message = """
            |Привет, ${updateDTO.userTelegramName}!
            |
            |Твоя группа: ❌
            |Срок для уведомления: ❌
            |Уведомления: ❌
            |
            |Воспользуйся меню, чтобы выставить группу, срок и настроить уведомления.
            |""".trimMargin()

        return SendMessageBuilder.builder()
            .message(message)
            .replyMarkup(ReplyMenuUtil.mainMenu(student.telegramName!!))
            .build(updateDTO.chatId)
    }

    fun processCreateParaCommand(updateDTO: UpdateDTO, student: Student): SendMessage {
        if (!student.isHeadman) {
            return SendMessageBuilder.builder()
                .message("Отказано в создании расписания. Вы не являетесь старостой!")
                .replyMarkup(ReplyMenuUtil.mainMenu(student.telegramName!!))
                .build(updateDTO.chatId);
        }

        queueSender.sendGetTeachersAndSubjects(student.telegramChatId!!.toString())

        return SendMessageBuilder.builder()
            .message("Запрос отправлен...")
            .replyMarkup(ReplyMenuUtil.mainMenu(student.telegramName!!))
            .build(updateDTO.chatId)
    }

    fun processGroupCommand(updateDTO: UpdateDTO, student: Student): SendMessage {
        val allGroupNames = groupRepository.findAll().mapNotNull { it.name }.toMutableList()

        return SendMessageBuilder.builder()
            .message("Выбери группу из предложенных:")
            .replyMarkup(ReplyMenuUtil.groupSelector(allGroupNames))
            .build(updateDTO.chatId)
    }

    fun processPeriodCommand(updateDTO: UpdateDTO, student: Student): SendMessage {
        val allPeriods = periodRepository.findAll().mapNotNull { it.daysCount }.toMutableList()

        return SendMessageBuilder.builder()
            .message("Выбери срок из предложенных:")
            .replyMarkup(ReplyMenuUtil.periodSelector(allPeriods))
            .build(updateDTO.chatId)
    }

    fun processNoticeNow(updateDTO: UpdateDTO, student: Student): SendMessage? {
        if (student.telegramName != "sullensoul") {
            return SendMessageBuilder.builder()
                .message("Ты не администратор!")
                .replyMarkup(ReplyMenuUtil.mainMenu(student.telegramName!!))
                .build(updateDTO.chatId)
        }

        queueSender.sendShortCommand(
            student.telegramChatId!!.toString(),
            "send_now"
        )

        return null
    }

    fun processNotificationCommand(updateDTO: UpdateDTO, student: Student): SendMessage {
        return SendMessageBuilder.builder()
            .message("Что сделать с уведомлениями?")
            .replyMarkup(ReplyMenuUtil.notificationSelector())
            .build(updateDTO.chatId)
    }
}