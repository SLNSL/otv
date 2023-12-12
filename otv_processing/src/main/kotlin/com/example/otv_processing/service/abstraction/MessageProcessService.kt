package com.example.otv_processing.service.abstraction

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

interface MessageProcessService {
    fun process(update: Update): SendMessage?
}