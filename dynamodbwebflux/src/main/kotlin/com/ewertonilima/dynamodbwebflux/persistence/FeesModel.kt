package com.ewertonilima.dynamodbwebflux.persistence

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.lang.reflect.Type

@DynamoDbBean
data class FeesModel(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("cod_prod_crto_cred")
    var dn: Int? = null,
    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("dat_atui")
    var dateUpdate: String? = null,
    @get:DynamoDbAttribute("sig_cobr")
    var acronymCharge: String? = null,
    @get:DynamoDbAttribute("vlr_minm")
    var minimumValue: String? = null,
    @get:DynamoDbAttribute("vlr_maxi")
    var maximumValue: String? = null,
    @get:DynamoDbConvertedBy(RangeListConverter::class)
    @get:DynamoDbAttribute("faix_prod")
    var productRanges: List<Range>? = null
)

class RangeListConverter : AttributeConverter<List<Range>> {
    private val objectMapper = jacksonObjectMapper()

    override fun transformFrom(input: List<Range>): AttributeValue {
        return AttributeValue.builder().s(objectMapper.writeValueAsString(input)).build()
    }

    override fun transformTo(input: AttributeValue): List<Range> {
        return objectMapper.readValue(
            input.s(),
            objectMapper.typeFactory.constructCollectionType(List::class.java, Range::class.java)
        )
    }

    override fun type(): EnhancedType<List<Range>> {
        return EnhancedType.listOf(Range::class.java)
    }

    override fun attributeValueType(): AttributeValueType {
        return AttributeValueType.S
    }
}

data class Range(
    @get:DynamoDbAttribute("faix_enqu")
    var range: String? = null,
    @get:DynamoDbAttribute("vlr_mdna")
    var averageValue: String? = null,
    @get:DynamoDbAttribute("pct_clie")
    var clientPercentage: String? = null
)

