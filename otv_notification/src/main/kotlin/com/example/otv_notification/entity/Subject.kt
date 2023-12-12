package com.example.otv_notification.entity

import com.example.otv_notification.entity.relation.SubjectTeacher
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "subject")
class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

    var name: String? = null

    @OneToMany(mappedBy = "subject", cascade = [CascadeType.ALL])
    @JsonIgnore
    var subjectTeachers: List<SubjectTeacher>? = null

}