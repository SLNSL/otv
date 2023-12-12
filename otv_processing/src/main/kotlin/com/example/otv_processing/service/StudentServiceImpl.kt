package com.example.otv_processing.service

import com.example.otv_processing.entity.Student
import com.example.otv_processing.messaging.QueueSender
import com.example.otv_processing.repository.StudentRepository
import com.example.otv_processing.service.abstraction.StudentService
import lombok.extern.slf4j.Slf4j
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.stereotype.Service

@Service
@Slf4j
class StudentServiceImpl(
    private val studentRepository: StudentRepository
) : StudentService {
    private val queueSender = QueueSender()

    override fun saveWithNewGroup(student: Student) {
        log.info("Пользователь ${student.telegramName} изменил группу")
        student.lastCommand = null
        studentRepository.save(student)
        if (student.isNotificationEnabled) {
            queueSender.sendChangeMessage(
                chatId = student.telegramChatId!!.toString(),
                group = student.group!!.name!!,
               noticePeriod = student.noticePeriod!!.daysCount!!.toString())
        }
    }

    override fun saveWithNewPeriod(student: Student) {
        log.info("Пользователь ${student.telegramName} изменил период")
        student.lastCommand = null
        studentRepository.save(student)
        if (student.isNotificationEnabled) {
            queueSender.sendChangeMessage(
                chatId = student.telegramChatId!!.toString(),
                noticePeriod = student.noticePeriod!!.daysCount!!.toString(),
                group = student.group!!.name!!
            )
        }
    }

    override fun saveAndDeleteSchedule(student: Student) {
        log.info("Удаление расписание для пользователя ${student.telegramName}")
        student.lastCommand = null
        studentRepository.save(student)
        queueSender.sendDeleteMessage(chatId = student.telegramChatId!!.toString())
    }

    override fun saveAndCreateSchedule(student: Student) {
        log.info("Создание расписание для пользователя ${student.telegramName}")
        student.lastCommand = null
        studentRepository.save(student)
        queueSender.sendCreateMessage(
            student.telegramChatId!!.toString(),
            student.group!!.name!!,
            student.noticePeriod!!.daysCount!!.toString()
        )
    }

    override fun justSave(student: Student) {
        log.info("Просто сохранение в базу пользователя ${student.telegramName}")
        student.lastCommand = null
        studentRepository.save(student)
    }
}