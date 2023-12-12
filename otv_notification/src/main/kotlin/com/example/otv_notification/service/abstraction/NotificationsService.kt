package com.example.otv_notification.service.abstraction

import com.example.otv_notification.entity.Para

interface NotificationsService {

    fun schedule(): Int

    fun nowSchedule(): Int

    fun createNotifications(noticePeriod: String, group: String, chatId: String)

    fun changeNotifications(noticePeriod: String, group: String, chatId: String)

    fun refreshNotifications(group: String, newParas: List<Para>, groupChatIdsToNoticePeriod: List<Pair<Long?, Int?>>): Int

    fun deleteNotifications(chatId: String)
}