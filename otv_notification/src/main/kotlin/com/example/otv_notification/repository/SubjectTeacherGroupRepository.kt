package com.example.otv_notification.repository

import com.example.otv_notification.entity.relation.SubjectTeacher
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubjectTeacherGroupRepository: JpaRepository<SubjectTeacher, Long> {
    fun findAllByGroupName(groupName: String): List<SubjectTeacher>
}