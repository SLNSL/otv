package com.example.otv_processing.entity

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "student")
class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "notice_period")
    var noticePeriod: Period? = null

    @ManyToOne
    @JoinColumn(name = "group_id")
    var group: Group? = null

    var telegramName: String? = null
    var telegramChatId: Long? = null
    var lastCommand: String? = null

    var isNotificationEnabled: Boolean = false

    var isHeadman: Boolean = false
}