package com.ewertonilima.dynamodbwebflux.controller

import com.ewertonilima.dynamodbwebflux.persistence.FeesModel
import com.ewertonilima.dynamodbwebflux.persistence.FeesRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/fees")
class FeesController(
    private val feesRepository: FeesRepository
) {
    @PostMapping
    fun save(@RequestBody feesModel: FeesModel): ResponseEntity<Any> {
        val entity = feesRepository.getDynamoDbItem(feesModel.dn, feesModel.dateUpdate)
        if (entity.get() != null) {
            feesRepository.updateDynamoDbItem(feesModel)
        } else {
            feesRepository.save(feesModel)
        }
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("/batch")
    fun saveAll(@RequestBody feesList: List<FeesModel>): ResponseEntity<Any> {
        feesList.forEach {
            val entity = feesRepository.getDynamoDbItem(it.dn, it.dateUpdate)
            if (entity.get() != null) {
                feesRepository.updateDynamoDbItem(it)
            } else {
                feesRepository.save(it)
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("/{dn}")
    fun findById(@PathVariable("dn") dn: Int): ResponseEntity<Any> {

        val fee = feesRepository.getItemsByKey(dn)

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