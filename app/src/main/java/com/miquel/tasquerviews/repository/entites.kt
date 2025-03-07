package com.miquel.tasquerviews.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date


@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val email: String,
    val password: String
) : Serializable


@Entity(
    tableName = "task_item",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var date: Date? = null,
    var description: String? = null,
    var isDone: Boolean? = false,
    var duration: Int? = null,
    @ColumnInfo(index = true)
    var userId: String? = null,
    var email: String? = null,
    var link: String? = null
) : Serializable {
    // No-argument constructor (required by Firestore)
    constructor() : this(null, null,null, null, null, null, null, null)
}