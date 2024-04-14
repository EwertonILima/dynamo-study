package com.ewertonilima.dynamodb.controller

import com.ewertonilima.dynamodb.entity.Tbjw6003TxaProd
import io.awspring.cloud.dynamodb.DynamoDbTemplate
import org.springframework.http.HttpStatus
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
@RequestMapping("/v1/fees")
class FeesController(
    private val dynamoDbTemplate: DynamoDbTemplate
) {
    @PostMapping
    fun save(@RequestBody feesModel: Tbjw6003TxaProd): ResponseEntity<Any> {
        dynamoDbTemplate.save(feesModel)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<Tbjw6003TxaProd>> {
        val feesList = dynamoDbTemplate.scanAll(Tbjw6003TxaProd::class.java)

        return ResponseEntity.ok(feesList.items().stream().toList())
    }

    @GetMapping("/{dn}")
    fun findById(@PathVariable("dn") dn: Int): ResponseEntity<List<Tbjw6003TxaProd>> {
        val key = Key.builder().partitionValue(dn).build()

        val condition = QueryConditional.keyEqualTo(key)

        val query = QueryEnhancedRequest.builder()
            .queryConditional(condition)
            .build()

        val fees = dynamoDbTemplate.query(query, Tbjw6003TxaProd::class.java)

        return ResponseEntity.ok(fees.items().stream().toList())
    }

}