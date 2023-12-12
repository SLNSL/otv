package com.example.otv_notification.repository

import com.example.otv_notification.entity.Subject
import com.example.otv_notification.entity.Teacher
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TeacherRepository : JpaRepository<Teacher, Long> {

    fun findByName(name: String): Teacher?
}
