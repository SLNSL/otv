package com.example.otv_notification.consumer

import com.example.otv_notification.repository.SubjectRepository
import com.example.otv_notification.repository.TeacherRepository
import com.example.otv_notification.service.TelegramSender
import com.example.otv_notification.service.abstraction.NotificationsService
import com.example.otv_notification.service.abstraction.ParaService
import com.example.otv_notification.util.getConnectionFactory
import jakarta.annotation.PostConstruct
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.springframework.stereotype.Component
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.Session
import javax.jms.TextMessage

private val queueName = "para_queue"

@Component
class ParaQueueConsumer(
    private val teacherRepository: TeacherRepository,
    private val subjectRepository: SubjectRepository,
    private val telegramSender: TelegramSender,
    private val paraService: ParaService
) : MessageListener {

    @PostConstruct
    fun init() {
        val connection = getConnectionFactory().createConnection()
        val session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE)
        val queue = session.createQueue(queueName);
        val consumer = session.createConsumer(queue)
        consumer.messageListener = this
        connection.start()
        Thread.sleep(1000)
    }

    override fun onMessage(m: Message?) {
        val message = (m as TextMessage).text
        try {
            println("$queueName Message received: $message")
            val jsonMessage = JSONObject(message)

            val type = (jsonMessage.get("type") ?: throw JSONException("type is null")).toString()

            if (type == "get") {
                handleGetType(jsonMessage)
            } else {
                handleCreateType(jsonMessage)
            }
        } catch (e: JSONException) {
            println(
                "При парсе полученного сообщения в JSON произошла ошибка. Сообщение некорректное и все равно будет удалено из очереди: $message"
            )
        } finally {
            m.acknowledge()
        }
    }

    private fun handleGetType(j: JSONObject) {
        val chatId = j.get("chatId") ?: throw JSONException("chatId is null")

        val teacherList = teacherRepository.findAll()
            .map { it.name }
        val subjectList = subjectRepository.findAll()
            .map { it.name }

        val teachers = teacherList.joinToString("\n")
        val subjects = subjectList.joinToString("\n")

        var answerText = """
                |Список предметов:
                |$subjects
              
                |Список преподавателей:
                |$teachers
            """.trimMargin()

        telegramSender.send(chatId.toString(), answerText)

        answerText = """
                Пожалуйста оформи расписание пар, строго в виде:
                
                Расписание:
                Имя предмета; ФИО преподавателя;
                [Дата и время пары
                Дата и время пары
                Дата и время пары];
                
                (Формат даты: YYYY-MM-DDTHH:MM:SS.ffffff)
                
                Например:
                
                Расписание:
                СОА; Егошин А.В.;
                [2023-12-13T20:00:00.000000
                2023-12-14T20:00:00.000000
                2024-12-02T15:30:00.000000];
                ИБ; Маркина Т.А.;
                [2023-12-13T20:00:00.000000
                2023-12-14T20:00:00.000000
                2024-12-02T15:30:00.000000];
            """.trimIndent()
        telegramSender.send(chatId.toString(), answerText)
    }

    private fun handleCreateType(j: JSONObject) {
        val chatId = j.get("chatId") ?: throw JSONException("chatId is null")
        val data = j.get("data") ?: throw JSONException("data is null")
        val group = j.get("group") ?: throw JSONException("group is null")
        val groupChatIdsToNoticePeriodAsJsonShit =
            (j.get("groupChatIdsToNoticePeriod") ?: throw JSONException("groupChatIdsToNoticePeriod is null"))
                    as JSONArray

        val groupChatIdsToNoticePeriod: ArrayList<Pair<Long?, Int?>> = arrayListOf()

        groupChatIdsToNoticePeriodAsJsonShit.map {
            it as JSONObject
        }.forEach {
            val first = it.getLong("first")
            val second = it.getInt("second")
            groupChatIdsToNoticePeriod.add(Pair(first, second))
        }
        val countNotifications = paraService.createParasFromRequest(
            data.toString(),
            group.toString(),
            chatId.toString(),
            groupChatIdsToNoticePeriod)

        val answerText = "Успешно создалось $countNotifications".trimMargin()

        telegramSender.send(chatId.toString(), answerText)

    }
}