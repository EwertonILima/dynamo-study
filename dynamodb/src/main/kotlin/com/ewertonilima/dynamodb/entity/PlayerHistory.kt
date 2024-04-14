package com.ewertonilima.dynamodb.entity

import com.ewertonilima.dynamodb.dto.ScoreDto
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.Instant
import java.util.*

@DynamoDbBean
data class PlayerHistory(
        @get:DynamoDbPartitionKey
        @get:DynamoDbAttribute("player_id")
        var playerId: String? = null,
        @get:DynamoDbSortKey
        @get:DynamoDbAttribute("game_id")
        var gameId: UUID?= null,
        @get:DynamoDbAttribute("score")
        var score: Double?= null,
        @get:DynamoDbAttribute("created_at")
        var createdAt: Instant?= null
) {

    companion object {
        fun fromScore(username: String, scoreDto: ScoreDto): PlayerHistory {
            return PlayerHistory(
                    playerId = username,
                    gameId = UUID.randomUUID(),
                    score = scoreDto.score,
                    createdAt = Instant.now()
            )
        }
    }
}