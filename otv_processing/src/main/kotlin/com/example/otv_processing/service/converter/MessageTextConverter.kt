package com.example.otv_processing.service.converter

import com.example.otv_processing.exception.MessageProcessException

class MessageTextConverter {
    companion object {
        fun convertPeriodMessageToNumber(period: String): String {
            var result = period
            if (period.toIntOrNull() == null) {
                val numberRegex = Regex("""\d+""")
                result = numberRegex.find(period)?.value
                    ?: throw MessageProcessException("Период не найден. Пожалуйста, выберите из предложенных")
            }
            return result
        }

        fun convertNotificationToBoolean(notification: String): Boolean {
            return notification.startsWith("Включить")
        }
    }
}