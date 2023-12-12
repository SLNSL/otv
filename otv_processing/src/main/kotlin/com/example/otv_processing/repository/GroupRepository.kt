package com.example.otv_processing.repository

import com.example.otv_processing.entity.Group
import com.example.otv_processing.entity.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository : JpaRepository<Group, Long> {

    fun findByName(name: String): Group?


}