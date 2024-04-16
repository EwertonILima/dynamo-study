package com.ewertonilima.dynamodbwebflux.persistence

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.*
import software.amazon.awssdk.enhanced.dynamodb.model.*
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException
import java.util.concurrent.CompletableFuture

@Repository
class FeesRepository(
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    var table: DynamoDbAsyncTable<FeesModel> = dynamoDbEnhancedAsyncClient.table(TABLE_NAME, tableSchema)

    fun save(feesModel: FeesModel) {
        try {
            table.putItemWithResponse(PutItemEnhancedRequest.builder(FeesModel::class.java).item(feesModel).build())
                .get()
            logger.info("Saving content to DynamoDb")
        } catch (e: Exception) {
            logger.error("Unexpected error occurred: $e")
        }
    }

    fun saveAll(feesList: List<FeesModel>) {
        try {
            val feesTable: MappedTableResource<FeesModel> = dynamoDbEnhancedAsyncClient.table(TABLE_NAME, tableSchema)

            // Convert list of FeesModel items into a list of WriteRequest objects
            val writeBatches = feesList.map { model ->
                WriteBatch.builder(FeesModel::class.java)
                    .mappedTableResource(feesTable)
                    .addPutItem(model)
                    .build()
            }

            // Create a BatchWriteItemEnhancedRequest with the prepared WriteBatch objects
            val batchWriteItemRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBatches)
                .build()
            // Execute batch write operation
            dynamoDbEnhancedAsyncClient.batchWriteItem(batchWriteItemRequest)
            logger.info("Saving content to DynamoDb")
        } catch (e: Exception) {
            logger.error("Unexpected error occurred: $e")
        } catch (e: DynamoDbException) {
            logger.error("Error occurred while saving items to DynamoDB: $e")
        }
    }

    fun getItemByKey(dn: Int): PagePublisher<FeesModel>? {
        val key = Key.builder().partitionValue(dn).build()

        val condition = QueryConditional.keyEqualTo(key)

        val query = QueryEnhancedRequest.builder()
            .queryConditional(condition)
            .build()

        return table.query(query) // Assuming 'key' is the primary key value
    }

    fun getDynamoDbItem(dn: Int, dateUpdate: String): CompletableFuture<FeesModel> {
        val keyToGet = Key.builder().partitionValue(dn).sortValue(dateUpdate).build()

        val request = GetItemEnhancedRequest.builder()
            .key(keyToGet)
            .build()

        var returnedItem = CompletableFuture<FeesModel>()
        try {
            returnedItem = table.getItem(request)

            if (returnedItem != null) {
                logger.info("Amazon DynamoDB table attributes: ${returnedItem.get()}")

            } else {
                logger.info("No item found with the key $returnedItem}!") // String interpolation for clearer message
            }
        } catch (e: DynamoDbException) {
            System.err.println(e.message) // Use System.err for error messages
        }
        return returnedItem
    }


    companion object {
        private val tableSchema = TableSchema.fromBean(FeesModel::class.java)
        private const val TABLE_NAME = "tbjw6003_txa_prod"
    }
}