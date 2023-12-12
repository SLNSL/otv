package com.example.otv_notification.repository

import com.example.otv_notification.entity.Para
import com.example.otv_notification.entity.relation.SubjectTeacher
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ParaRepository: JpaRepository<Para, Long> {
    fun findBySubjectTeacherIn(subjectTeacherSet: Set<SubjectTeacher>): List<Para>
}