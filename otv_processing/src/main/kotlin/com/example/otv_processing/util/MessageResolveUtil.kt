package com.example.otv_processing.util

class MessageResolveUtil {

    companion object {
        private val GROUP_PATTERN = "^P\\d{3,6}\$".toRegex()
        private val PERIOD_PATTERN = "^За \\d+ дней\$".toRegex()
        private val NOTIFICATIONS_PATTERN = "^(Включить|Выключить)( уведомления)?\$".toRegex()

        private val PICK_GROUP_MESSAGE_VARIANTS = listOf("Выбрать группу", "Выбрать другую группу")
        private val PICK_PERIOD_MESSAGE_VARIANTS = listOf("Выбрать срок", "Выбрать другой срок")
        private val PICK_NOTIFICATIONS_MESSAGE_VARIANTS = listOf("Настройки уведомлений")


        fun isStartCommand(text: String) = (text == "/start")
        fun isGroupCommand(text: String) = (text in PICK_GROUP_MESSAGE_VARIANTS)
        fun isPeriodCommand(text: String) = (text in PICK_PERIOD_MESSAGE_VARIANTS)
        fun isNotificationsCommand(text: String) = (text in PICK_NOTIFICATIONS_MESSAGE_VARIANTS)

        fun isGroupSelectMessage(text: String, lastCommand: String?) = isGroupCommand(lastCommand ?: "") && GROUP_PATTERN.matches(text)
        fun isPeriodSelectMessage(text: String, lastCommand: String?) = isPeriodCommand(lastCommand ?: "") && PERIOD_PATTERN.matches(text)
        fun isNotificationsSelectMessage(text: String, lastCommand: String?) = isNotificationsCommand(lastCommand?: "") && NOTIFICATIONS_PATTERN.matches(text)

    }

}