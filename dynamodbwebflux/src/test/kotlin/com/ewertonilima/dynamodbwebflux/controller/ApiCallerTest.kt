package com.ewertonilima.dynamodbwebflux.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.time.LocalDate
import java.util.Random


class ApiCallerTest {

    private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build()
    private val objectMapper = ObjectMapper()
    private val random = Random()

    @Test
    fun `test save endpoint with random payload`() {

        fun generatePayload(): String {
            // Generate random values for the payload
            val dn = random.nextInt(1000, 9999)
            val dateUpdate = LocalDate.now().toString()
            val acronymCharge = generateRandomString(3)
            val minimumValue = String.format("%.2f", random.nextDouble(0.0, 1000.0))
            val maximumValue = String.format("%.2f", random.nextDouble(0.0, 1000.0))

            // Generate random product ranges
            val productRanges = mutableListOf<Map<String, Any>>()
            for (i in 1..4) {
                val range = i.toString()
                val averageValue = String.format("%.2f", random.nextDouble(0.0, 1000000.0))
                val clientPercentage = String.format("%.4f", random.nextDouble(0.0, 1.0))
                val productRange =
                    mapOf("range" to range, "averageValue" to averageValue, "clientPercentage" to clientPercentage)
                productRanges.add(productRange)
            }

            // Create the payload map
            val payloadMap = mapOf(
                "dn" to dn,
                "dateUpdate" to dateUpdate,
                "acronymCharge" to acronymCharge,
                "minimumValue" to minimumValue,
                "maximumValue" to maximumValue,
                "productRanges" to productRanges
            )

            // Convert payload map to JSON string
            return objectMapper.writeValueAsString(payloadMap)
        }

        // Send POST request to the API endpoint
        for (i in 1..5) {
            client.post()
                .uri("http://localhost:8080/v1/fees")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(generatePayload()))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
        }
    }

    // Function to generate a random string of specified length
    private fun generateRandomString(length: Int): String {
//        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val charset = ('A'..'Z')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
}