package com.example.otv_processing.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "university_group")
class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

    var name: String? = null

    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL])
    @JsonIgnore
    var students: List<Student>? = null
}