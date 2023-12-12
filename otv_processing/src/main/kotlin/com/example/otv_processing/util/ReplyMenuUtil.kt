package com.example.otv_processing.util

import com.example.otv_processing.exception.MessageProcessException
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class ReplyMenuUtil {

    companion object {
        fun mainMenu(): ReplyKeyboardMarkup {
            val replyKeyboardMarkup = ReplyKeyboardMarkup()

            replyKeyboardMarkup.selective = true
            replyKeyboardMarkup.resizeKeyboard = true
            replyKeyboardMarkup.oneTimeKeyboard = false

            val keyboard = ArrayList<KeyboardRow>()

            val row1 = KeyboardRow()
            row1.add("Выбрать группу")
            row1.add("Выбрать срок")

            val row2 = KeyboardRow()
            row2.add("Настройки уведомлений")

            keyboard.add(row1)
            keyboard.add(row2)
            replyKeyboardMarkup.keyboard = keyboard

            return replyKeyboardMarkup
        }

        fun groupSelector(groupList: List<String>): ReplyKeyboardMarkup {
            if (groupList.isEmpty()) {
                throw MessageProcessException("Нет доступных групп в базе. Попробуйте позднее")
            }
            val replyKeyboardMarkup = ReplyKeyboardMarkup()

            replyKeyboardMarkup.selective = true
            replyKeyboardMarkup.resizeKeyboard = true
            replyKeyboardMarkup.oneTimeKeyboard = false

            val keyboard = ArrayList<KeyboardRow>()

            val rowList = ArrayList<KeyboardRow>()
            for (i in groupList.indices) {
                if (i % 2 == 0) rowList.add(KeyboardRow())

                val lastRow = rowList[rowList.size - 1]
                lastRow.add(groupList[i])
            }

            keyboard.addAll(rowList)
            replyKeyboardMarkup.keyboard = keyboard

            return replyKeyboardMarkup
        }

        fun periodSelector(periodList: List<Int>): ReplyKeyboardMarkup {
            if (periodList.isEmpty()) {
                throw MessageProcessException("Нет доступных периодов в базе. Попробуйте позднее")
            }

            val periodTextList = periodList.map { "За $it дней" }

            val replyKeyboardMarkup = ReplyKeyboardMarkup()

            replyKeyboardMarkup.selective = true
            replyKeyboardMarkup.resizeKeyboard = true
            replyKeyboardMarkup.oneTimeKeyboard = false

            val keyboard = ArrayList<KeyboardRow>()

            val rowList = ArrayList<KeyboardRow>()
            for (i in periodTextList.indices) {
                if (i % 2 == 0) rowList.add(KeyboardRow())

                val lastRow = rowList[rowList.size - 1]
                lastRow.add(periodTextList[i])
            }

            keyboard.addAll(rowList)
            replyKeyboardMarkup.keyboard = keyboard

            return replyKeyboardMarkup
        }

        fun notificationSelector(): ReplyKeyboardMarkup {
            val replyKeyboardMarkup = ReplyKeyboardMarkup()

            replyKeyboardMarkup.selective = true
            replyKeyboardMarkup.resizeKeyboard = true
            replyKeyboardMarkup.oneTimeKeyboard = false

            val keyboard = ArrayList<KeyboardRow>()

            val row = KeyboardRow()
            row.add("Включить уведомления")
            row.add("Выключить уведомления")
            keyboard.add(row)
            replyKeyboardMarkup.keyboard = keyboard

            return replyKeyboardMarkup
        }
    }

}