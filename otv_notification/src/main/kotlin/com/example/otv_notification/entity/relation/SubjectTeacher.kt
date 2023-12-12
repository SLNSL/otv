package com.example.otv_notification.entity.relation

import com.example.otv_notification.entity.Subject
import com.example.otv_notification.entity.Teacher
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "subject_teacher_group")
class SubjectTeacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

    var groupName: String? = null

    @ManyToOne
    @JoinColumn(name = "subject_id")
    var subject: Subject? = null

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    var teacher: Teacher? = null

}