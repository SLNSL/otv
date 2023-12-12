package com.example.otv_notification.repository

import com.example.otv_notification.entity.Notification
import com.example.otv_notification.entity.Subject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface SubjectRepository : JpaRepository<Subject, Long> {
    fun findByName(name: String): Subject?

}