package com.fitx.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ActivitySessionWithPoints(
    @Embedded val session: ActivitySessionEntity,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "sessionId"
    )
    val points: List<ActivityPointEntity>
)

