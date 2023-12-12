package com.example.otv_notification.service

import com.example.otv_notification.entity.Notification
import com.example.otv_notification.entity.Para
import com.example.otv_notification.repository.NotificationRepository
import com.example.otv_notification.repository.ParaRepository
import com.example.otv_notification.repository.SubjectTeacherGroupRepository
import com.example.otv_notification.service.abstraction.NotificationsService
import jakarta.annotation.PostConstruct

import lombok.extern.slf4j.Slf4j
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Component
@Slf4j
@Transactional
class NotificationsServiceImpl(
    private val subjectTeacherGroupRepository: SubjectTeacherGroupRepository,
    private val paraRepository: ParaRepository,
    private val notificationRepository: NotificationRepository,
    private val telegramSender: TelegramSender
) : NotificationsService {

    @PostConstruct
    fun init() {

    }

    @Scheduled(cron = "0 0 8 * * *")
    override fun schedule(): Int {
        println("Началась выгрузка и отправка уведомлений: ${LocalTime.now()}")
        val notificationList = notificationRepository.findByNoticeDate(LocalDate.now())
        println("Выгружено ${notificationList.size} уведомлений")
        notificationList.forEach {
            telegramSender.send(it)
        }
        notificationRepository.deleteAll(notificationList)
        return notificationList.size
    }

    override fun nowSchedule(): Int {
        println("Началась срочная выгрузка и отправка всех уведомлений: ${LocalTime.now()}")
        val notificationList = notificationRepository.findAll()
        println("Выгружено ${notificationList.size} уведомлений")
        notificationList.forEach {
            telegramSender.send(it)
        }
        notificationRepository.deleteAll(notificationList)
        return notificationList.size
    }

    override fun createNotifications(noticePeriod: String, group: String, chatId: String) {
        val subjTeacherGroupList = subjectTeacherGroupRepository.findAllByGroupName(group).toSet()
        val classList = paraRepository.findBySubjectTeacherIn(subjTeacherGroupList)
        val notificationList = mutableListOf<Notification>()
        classList.forEach {
            val notificationAboutThisClass = Notification()
            notificationAboutThisClass.chatId = chatId
            notificationAboutThisClass.subjectTeacher = it.subjectTeacher
            notificationAboutThisClass.noticeDate = (it.date!!.toLocalDate().minusDays(noticePeriod.toLong()))
            notificationAboutThisClass.noticePeriod = noticePeriod
            notificationList.add(notificationAboutThisClass)
        }

        notificationRepository.saveAll(notificationList)
    }

    override fun changeNotifications(noticePeriod: String, group: String, chatId: String) {
        deleteNotifications(chatId)
        createNotifications(noticePeriod, group, chatId)
    }

    override fun refreshNotifications(
        group: String,
        newParas: List<Para>,
        groupChatIdsToNoticePeriod: List<Pair<Long?, Int?>>
    ): Int {

        val newNotifications = mutableListOf<Notification>()

        groupChatIdsToNoticePeriod.forEach {
            val chatId = it.first
            val noticePeriod = it.second

            newParas.forEach { para ->
                val notification = Notification()
                notification.chatId = chatId?.toString()
                notification.noticePeriod = noticePeriod?.toString()

                notification.noticeDate = para.date!!.toLocalDate().plusDays(noticePeriod!!.toLong())
                notification.subjectTeacher = para.subjectTeacher

                newNotifications.add(notification)
            }
        }

        return notificationRepository.saveAll(newNotifications).size
    }

    override fun deleteNotifications(chatId: String) {
        notificationRepository.deleteAllByChatId(chatId)
    }
}