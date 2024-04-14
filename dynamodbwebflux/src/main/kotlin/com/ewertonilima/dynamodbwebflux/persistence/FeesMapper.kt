package com.ewertonilima.dynamodbwebflux.persistence

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun FeesModel.toAttributeValueMap(): Map<String, AttributeValue> {
    val attributeMap = mutableMapOf<String, AttributeValue>()

    // Add each property of FeesModel to the attribute map
    attributeMap["dn"] = AttributeValue.builder().n(this.dn.toString()).build()
    attributeMap["dateUpdate"] = AttributeValue.builder().s(this.dateUpdate.toString()).build()
    attributeMap["acronymCharge"] = AttributeValue.builder().s(this.acronymCharge).build()
    attributeMap["minimumValue"] = AttributeValue.builder().n(this.minimumValue.toString()).build()
    attributeMap["maximumValue"] = AttributeValue.builder().n(this.maximumValue.toString()).build()
    // Add other properties as needed

    // Convert productRanges to a list of maps
    val productRanges = this.productRanges?.map { range ->
        mapOf(
            "range" to AttributeValue.builder().s(range.range).build(),
            "averageValue" to AttributeValue.builder().n(range.averageValue.toString()).build(),
            "clientPercentage" to AttributeValue.builder().n(range.clientPercentage.toString()).build()
        )
    }
    if (productRanges != null) {
        attributeMap["productRanges"] =
            AttributeValue.builder().l(productRanges.map { AttributeValue.builder().m(it).build() }).build()
    }

    return attributeMap
}
