package com.example.otv_notification.entity

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

}