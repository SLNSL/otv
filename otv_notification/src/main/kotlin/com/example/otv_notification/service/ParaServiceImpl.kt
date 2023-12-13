package com.example.otv_notification.service

import com.example.otv_notification.entity.Para
import com.example.otv_notification.entity.relation.SubjectTeacher
import com.example.otv_notification.repository.ParaRepository
import com.example.otv_notification.repository.SubjectRepository
import com.example.otv_notification.repository.SubjectTeacherGroupRepository
import com.example.otv_notification.repository.TeacherRepository
import com.example.otv_notification.service.abstraction.NotificationsService
import com.example.otv_notification.service.abstraction.ParaService
import jakarta.ws.rs.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ParaServiceImpl(
    private val subjectTeacherGroupRepository: SubjectTeacherGroupRepository,
    private val teacherRepository: TeacherRepository,
    private val subjectRepository: SubjectRepository,
    private val paraRepository: ParaRepository,
    private val notificationsService: NotificationsService
): ParaService {

    override fun createParasFromRequest(data: String, group: String, chatId: String, groupChatIdsToNoticePeriod: List<Pair<Long?, Int?>>): Int {
        val dataPartitions = data.trim()
            .split("Расписание:")[1]
            .split(';')

        var teacherBuffer = ""
        var subjectBuffer = ""
        val newParas = mutableListOf<Para>()
        for (ind in dataPartitions.indices) {
            if (ind % 3 == 0) {
                subjectBuffer = dataPartitions[ind].trim()
            } else if (ind % 3 == 1) {
                teacherBuffer = dataPartitions[ind].trim()
            } else {
                val dates = dataPartitions[ind].trim().substring(1, dataPartitions[ind].length - 2)
                newParas.addAll(createParas(teacherBuffer, subjectBuffer, group, dates))
                teacherBuffer = ""
                subjectBuffer = ""
            }
        }

        return notificationsService.refreshNotifications(group, newParas, groupChatIdsToNoticePeriod)
    }

    @Transactional
    fun createParas(teacher: String, subject: String, group: String, dates: String): List<Para> {
        val teacherEntity = teacherRepository.findByName(teacher)
            ?: throw NotFoundException("$teacher не найден")

        val subjectEntity = subjectRepository.findByName(subject)
            ?: throw NotFoundException("$subject не найден")

        var subjectTeacherGroup = SubjectTeacher()
        subjectTeacherGroup.subject = subjectEntity
        subjectTeacherGroup.teacher = teacherEntity
        subjectTeacherGroup.groupName = group


        val frombd = subjectTeacherGroupRepository.findAll()
            .filter { it.groupName == group && it.teacher!!.id == teacherEntity.id && it.subject!!.id == subjectEntity.id }


        if (frombd.isEmpty()) {
            subjectTeacherGroup = subjectTeacherGroupRepository.save(subjectTeacherGroup)
        } else {
            subjectTeacherGroup = frombd[0]
        }


        val newParas = mutableListOf<Para>()
        val allparas = paraRepository.findAll()
        val allCreatedParas = mutableListOf<Para>()
        dates.split("\n")
            .map { LocalDateTime.parse(it) }
            .forEach { date ->
                val para = Para()
                para.date = date
                para.subjectTeacher = subjectTeacherGroup

                if (allparas.none { it.subjectTeacher!!.id == subjectTeacherGroup.id && it.date == date }) {
                    newParas.add(para)
                }
                allCreatedParas.add(para)
            }
        paraRepository.saveAll(newParas)

        return allCreatedParas
    }
}