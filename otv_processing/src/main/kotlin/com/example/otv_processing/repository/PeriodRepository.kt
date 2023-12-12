package com.example.otv_processing.repository

import com.example.otv_processing.entity.Group
import com.example.otv_processing.entity.Period
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PeriodRepository : JpaRepository<Period, Long> {

    fun findByDaysCount(daysCount: Int): Period?

}