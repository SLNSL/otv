package com.example.otv_processing.service.abstraction

import com.example.otv_processing.entity.Student

interface StudentService {

    fun saveWithNewGroup(student: Student)

    fun saveWithNewPeriod(student: Student)

    fun saveAndDeleteSchedule(student: Student)

    fun saveAndCreateSchedule(student: Student)

    fun justSave(student: Student)
}