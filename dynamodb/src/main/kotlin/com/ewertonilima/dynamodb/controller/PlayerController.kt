package com.ewertonilima.dynamodb.controller

import com.ewertonilima.dynamodb.dto.ScoreDto
import com.ewertonilima.dynamodb.entity.PlayerHistory
import io.awspring.cloud.dynamodb.DynamoDbTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest

@RestController
@RequestMapping("/v1/players")
class PlayerController(
    @Autowired
    private val dynamoDbTemplate: DynamoDbTemplate
) {
    @PostMapping("/{username}/games")
    fun save(
        @PathVariable("username") username: String,
        @RequestBody scoreDto: ScoreDto
    ): ResponseEntity<Any> {

        val entity = PlayerHistory.fromScore(username, scoreDto)
        println("TESTE $entity")
        dynamoDbTemplate.save(entity)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("/{username}/games")
    fun listHistory(@PathVariable("username") username: String): ResponseEntity<List<PlayerHistory>> {
        val key = Key.builder().partitionValue(username).build()

        val condition = QueryConditional.keyEqualTo(key)

        val query = QueryEnhancedRequest.builder()
            .queryConditional(condition)
            .build()

        val history = dynamoDbTemplate.query(query, PlayerHistory::class.java)

        return ResponseEntity.ok(history.items().stream().toList())
    }

    @GetMapping("/{username}/games/{gameId}")
    fun findById(
        @PathVariable("username") username: String,
        @PathVariable("gameId") gameId: String
    ): ResponseEntity<Any> {
        val entity = dynamoDbTemplate.load(
            Key.builder()
                .partitionValue(username)
                .sortValue(gameId)
                .build(), PlayerHistory::class.java
        ) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT FOUND")

        return ResponseEntity.status(HttpStatus.OK).body(entity)
    }
}