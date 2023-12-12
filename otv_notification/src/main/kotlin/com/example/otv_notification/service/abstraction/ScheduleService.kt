package com.example.otv_notification.service.abstraction

interface ScheduleService {

    fun schedule()

    fun createSchedule(noticePeriod: String, group: String, chatId: String)

    fun changeSchedule(noticePeriod: String, group: String, chatId: String)

    fun deleteSchedule(chatId: String)
}