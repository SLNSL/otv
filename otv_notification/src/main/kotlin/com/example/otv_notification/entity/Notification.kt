package com.example.otv_notification.entity

import com.example.otv_notification.entity.relation.SubjectTeacher
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.time.LocalDate

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "notification")
class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "subject_teacher_group_id")
    var subjectTeacher: SubjectTeacher? = null

    var chatId: String? = null

    var noticeDate: LocalDate? = null

    var noticePeriod: String? = null
}