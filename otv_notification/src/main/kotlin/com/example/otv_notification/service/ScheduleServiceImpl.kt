package com.example.otv_notification.service

import com.example.otv_notification.entity.Notification
import com.example.otv_notification.repository.NotificationRepository
import com.example.otv_notification.repository.ParaRepository
import com.example.otv_notification.repository.SubjectTeacherGroupRepository
import com.example.otv_notification.service.abstraction.ScheduleService
import jakarta.annotation.PostConstruct

import lombok.extern.slf4j.Slf4j
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Component
@Slf4j
@Transactional
class ScheduleServiceImpl(
    private val subjectTeacherGroupRepository: SubjectTeacherGroupRepository,
    private val paraRepository: ParaRepository,
    private val notificationRepository: NotificationRepository,
    private val telegramSender: TelegramSender
) : ScheduleService {

    @PostConstruct
    fun init() {

    }

    //    @Scheduled(cron = "0 0 8 * * *")
    @Scheduled(fixedRate = 10000)
    // TODO: мб сделать многопоточкой
    override fun schedule() {
        println("Началась выгрузка и отправка уведомлений: ${LocalTime.now()}")
        val notificationList = notificationRepository.findByNoticeDate(LocalDate.now())
        println("Выгружено ${notificationList.size} уведомлений")
        notificationList.forEach {
            telegramSender.send(it)
        }
        notificationRepository.deleteAll()
    }

    override fun createSchedule(noticePeriod: String, group: String, chatId: String) {
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

    override fun changeSchedule(noticePeriod: String, group: String, chatId: String) {
        deleteSchedule(chatId)
        createSchedule(noticePeriod, group, chatId)
    }

    override fun deleteSchedule(chatId: String) {
        notificationRepository.deleteAllByChatId(chatId)
    }
}