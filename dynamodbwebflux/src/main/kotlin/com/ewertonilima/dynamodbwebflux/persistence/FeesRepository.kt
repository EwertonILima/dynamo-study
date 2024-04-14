package com.ewertonilima.dynamodbwebflux.persistence

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.MappedTableResource
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemResponse
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException
import software.amazon.awssdk.services.dynamodb.model.WriteRequest
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
            // Convert list of FeesModel items into a list of WriteRequest objects
            val writeBatches = feesList.map { model ->
                WriteBatch.builder(FeesModel::class.java)
                    .addPutItem(model)
                    .build()
            }

            // Create a BatchWriteItemEnhancedRequest with the prepared WriteBatch objects
            val batchWriteItemRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBatches)
                .build()

            // Execute batch write operation
            dynamoDbEnhancedAsyncClient.batchWriteItem(batchWriteItemRequest)

        } catch (e: Exception) {
            logger.error("Unexpected error occurred: $e")
        } catch (e: DynamoDbException) {
            logger.error("Error occurred while saving items to DynamoDB: $e")
        }

    }


    companion object {
        private val tableSchema = TableSchema.fromBean(FeesModel::class.java)
        private const val TABLE_NAME = "tbjw6003_txa_prod"
    }
}