package com.example.otv_notification.repository

import com.example.otv_notification.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface NotificationRepository : JpaRepository<Notification, Long>{
    fun findByNoticeDate(noticeDate: LocalDate): List<Notification>


    @Query("SELECT n FROM Notification n JOIN SubjectTeacher st ON n.subjectTeacher.id = st.id WHERE st.groupName = :group")
    fun findAllByGroup(group: String): List<Notification>

    fun deleteAllByChatId(chatId: String)
}