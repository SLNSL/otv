package com.example.otv_processing.service.handler

import com.example.otv_processing.dto.UpdateDTO
import com.example.otv_processing.entity.Student
import com.example.otv_processing.exception.MessageProcessException
import com.example.otv_processing.messaging.QueueSender
import com.example.otv_processing.repository.GroupRepository
import com.example.otv_processing.repository.PeriodRepository
import com.example.otv_processing.repository.StudentRepository
import com.example.otv_processing.service.abstraction.StudentService
import com.example.otv_processing.service.converter.MessageTextConverter.Companion.convertNotificationToBoolean
import com.example.otv_processing.service.converter.MessageTextConverter.Companion.convertPeriodMessageToNumber
import com.example.otv_processing.util.MessageResolveUtil.Companion.isGroupSelectMessage
import com.example.otv_processing.util.MessageResolveUtil.Companion.isNotificationsSelectMessage
import com.example.otv_processing.util.MessageResolveUtil.Companion.isPeriodSelectMessage
import com.example.otv_processing.util.ReplyMenuUtil
import com.example.otv_processing.util.SendMessageBuilder
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Component
class TextHandler(
    private val groupRepository: GroupRepository,
    private val studentService: StudentService,
    private val periodRepository: PeriodRepository
) {
    private val queueSender: QueueSender = QueueSender()

    fun processNotCommand(updateDTO: UpdateDTO, student: Student, preMessage: String? = ""): SendMessage {
        return when {
            isGroupSelectMessage(updateDTO.message, student.lastCommand) -> processGroupContained(updateDTO, student)
            isPeriodSelectMessage(updateDTO.message, student.lastCommand) -> processPeriodContained(updateDTO, student)
            isNotificationsSelectMessage(updateDTO.message, student.lastCommand) -> processNotificationContained(updateDTO, student)
            else -> processTrash(updateDTO, student, preMessage)
        }
    }

    fun processGroupContained(updateDTO: UpdateDTO, student: Student): SendMessage {
        try {
            val group = groupRepository.findByName(updateDTO.message)
                ?: throw MessageProcessException("Группа не найдена. Пожалуйста, выбери из предложенных")

            student.group = group
            studentService.saveWithNewGroup(student)

            return SendMessageBuilder.builder()
                .message("Теперь ваша группа - ${group.name}")
                .replyMarkup(ReplyMenuUtil.mainMenu())
                .build(updateDTO.chatId)

        } catch (e: MessageProcessException) {
            val allGroupNames = groupRepository.findAll().mapNotNull { it.name }
            return SendMessageBuilder.builder()
                .message(e.message!!)
                .replyMarkup(ReplyMenuUtil.groupSelector(allGroupNames))
                .build(updateDTO.chatId)
        }
    }

    fun processPeriodContained(updateDTO: UpdateDTO, student: Student): SendMessage {
        try {

            updateDTO.message = convertPeriodMessageToNumber(updateDTO.message)

            val noticePeriod = periodRepository.findByDaysCount(updateDTO.message.toInt())
                ?: throw MessageProcessException("Период не найден. Пожалуйста, выберите из предложенных")

            student.noticePeriod = noticePeriod
            studentService.saveWithNewPeriod(student)

            return SendMessageBuilder.builder()
                .message("Уведомления о дне сдаче будут приходить за ${noticePeriod.daysCount} дней до сдачи")
                .replyMarkup(ReplyMenuUtil.mainMenu())
                .build(updateDTO.chatId)

        } catch (e: MessageProcessException) {
            val allPeriods = periodRepository.findAll().mapNotNull { it.daysCount }
            return SendMessageBuilder.builder()
                .message(e.message!!)
                .replyMarkup(ReplyMenuUtil.periodSelector(allPeriods))
                .build(updateDTO.chatId)
        }
    }

    fun processNotificationContained(updateDTO: UpdateDTO, student: Student): SendMessage {
        try {

            if (student.group == null || student.noticePeriod == null) {
                throw MessageProcessException("Вы не можете включить/выключить уведомления, пока не заполните всю информацию о себе!")
            }

            val notificationChoice = convertNotificationToBoolean(updateDTO.message)
            val oldNotificationChoice = student.isNotificationEnabled

            student.isNotificationEnabled = notificationChoice

            if (!oldNotificationChoice && notificationChoice) {
                studentService.saveAndCreateSchedule(student)
            }

            if (oldNotificationChoice && !notificationChoice) {
                studentService.saveAndDeleteSchedule(student)
            }

            return SendMessageBuilder.builder()
                .message("Теперь уведомления ${if (notificationChoice) "включены" else "отключены"}")
                .replyMarkup(ReplyMenuUtil.mainMenu())
                .build(updateDTO.chatId)

        } catch (e: MessageProcessException) {
            return SendMessageBuilder.builder()
                .message(e.message!!)
                .replyMarkup(ReplyMenuUtil.mainMenu())
                .build(updateDTO.chatId)
        }
    }

    fun processTrash(updateDTO: UpdateDTO, student: Student, preMessage: String? = ""): SendMessage {
        val group = student.group?.name ?: "❌"
        val period = student.noticePeriod?.daysCount ?: "❌"
        val notifications = if (student.isNotificationEnabled) "✅" else "❌"

        val willGetNotifications = (group != "❌" && period != "❌" && notifications != "❌")

        val willGetNotificationsMessage =
            if (willGetNotifications) "Всё хорошо, вы подписаны на рассылку!"
            else {
                if (student.isNotificationEnabled) {
                    "Пожалуйста, заполните все поля для того, чтобы получать рассылку!"
                } else {
                    "Рассылка отключена"
                }
            }

        val message = preMessage +
                "\n\n" +
                "Твоя группа: $group \n" +
                "Срок для уведомления: $period дней\n" +
                "Уведомления: $notifications\n\n" +
                willGetNotificationsMessage

        return SendMessageBuilder.builder()
            .message(message)
            .replyMarkup(ReplyMenuUtil.mainMenu())
            .build(updateDTO.chatId)
    }



}