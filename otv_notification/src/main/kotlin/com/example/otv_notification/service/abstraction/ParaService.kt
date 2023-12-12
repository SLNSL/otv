package com.example.otv_notification.service.abstraction

interface ParaService {
    fun createParasFromRequest(data: String, group: String, chatId: String, groupChatIdsToNoticePeriod: List<Pair<Long?, Int?>>): Int

}