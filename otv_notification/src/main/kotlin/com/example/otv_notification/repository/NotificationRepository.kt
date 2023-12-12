package com.example.otv_notification.repository

import com.example.otv_notification.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface NotificationRepository : JpaRepository<Notification, Long>{
    fun findByNoticeDate(noticeDate: LocalDate): List<Notification>

    fun deleteAllByChatId(chatId: String)
}