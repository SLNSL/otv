package com.example.otv_processing.repository

import com.example.otv_processing.entity.Group
import com.example.otv_processing.entity.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : JpaRepository<Student, Long> {
    fun findByTelegramName(telegramName: String): Student?

    fun findAllByGroupAndIsNotificationEnabled(group: Group, isNotificationEnabled: Boolean): Set<Student>

    @Modifying
    @Query("UPDATE Student s SET s.lastCommand = :command WHERE s.telegramName = :telegramName")
    fun updateLastCommand(
        @Param("telegramName") telegramName: String,
        @Param("command") command: String
    ): Int

    fun existsByTelegramName(telegramName: String): Boolean


}