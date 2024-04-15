package com.ewertonilima.dynamodbwebflux.controller

import com.ewertonilima.dynamodbwebflux.persistence.FeesModel
import com.ewertonilima.dynamodbwebflux.persistence.FeesRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/fees")
class FeesController(
    private val feesRepository: FeesRepository
) {
    @PostMapping
    fun save(@RequestBody feesModel: FeesModel): ResponseEntity<Any> {
        feesRepository.save(feesModel)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("/batch")
    fun saveAll(@RequestBody feesList: List<FeesModel>): ResponseEntity<Any> {
        feesRepository.saveAll(feesList)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("/{dn}")
    fun findById(@PathVariable("dn") dn: Int): ResponseEntity<Any> {

        val fee = feesRepository.getItemByKey(dn)

        return ResponseEntity.status(HttpStatus.OK).body(fee?.items())
    }


//    @GetMapping
//    fun getAll(): ResponseEntity<List<Tbjw6003TxaProd>> {
//        val feesList = dynamoDbTemplate.scanAll(Tbjw6003TxaProd::class.java)
//
//        return ResponseEntity.ok(feesList.items().stream().toList())
//    }
//


}