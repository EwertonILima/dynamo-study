package com.ewertonilima.dynamodbwebflux.persistence

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.MappedTableResource
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException
import java.time.LocalDate
import java.util.concurrent.CompletableFuture


@Repository
class FeesRepository(
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private var table: DynamoDbAsyncTable<FeesModel> = dynamoDbEnhancedAsyncClient.table(TABLE_NAME, tableSchema)

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

    fun getItemsByKey(dn: Int?): PagePublisher<FeesModel>? {
        val key = Key.builder().partitionValue(dn).build()

        val condition = QueryConditional.keyEqualTo(key)

        val query = QueryEnhancedRequest.builder()
            .queryConditional(condition)
            .build()

        return table.query(query) // Assuming 'key' is the primary key value
    }

    fun getDynamoDbItem(dn: Int?, dateUpdate: String?): CompletableFuture<FeesModel> {
        val keyToGet = Key.builder().partitionValue(dn).sortValue(dateUpdate).build()

        val request = GetItemEnhancedRequest.builder()
            .key(keyToGet)
            .build()

        var returnedItem = CompletableFuture<FeesModel>()
        try {
            returnedItem = table.getItem(request)

            if (returnedItem.get() != null) {
                logger.info("Amazon DynamoDB table attributes: ${returnedItem.get()}")

            } else {
                logger.info("No item found with the key $returnedItem}!") // String interpolation for clearer message
            }
        } catch (e: DynamoDbException) {
            System.err.println(e.message) // Use System.err for error messages
        }
        return returnedItem
    }

    fun updateDynamoDbItem(feesModel: FeesModel) {
        val dateUpdate = LocalDate.now().toString()
        val updatedFee = feesModel.copy(dateUpdate = dateUpdate)

        val request = UpdateItemEnhancedRequest.builder(FeesModel::class.java)
            .item(updatedFee).build()

        try {
            table.updateItem(request)
        } catch (e: ResourceNotFoundException) {
            System.err.println(e.message)
            System.exit(1)
        } catch (e: DynamoDbException) {
            System.err.println(e.message)
            System.exit(1)
        }
    }

    fun deleteItem(dn: Int?, dateUpdate: String?){

        val key = Key.builder().partitionValue(dn).sortValue(dateUpdate).build()

        val keyToGet = HashMap<String, AttributeValue>()

        keyToGet["cod_prod_crto_cred"] = AttributeValue.builder()
            .s(dn.toString())
            .build()

        val deleteReq = DeleteItemEnhancedRequest.builder()
            .key(key)
            .build()

        try {
            table.deleteItemWithResponse(deleteReq).get()
        } catch (e: Exception) {
            logger.error("Error occurred while delete items to DynamoDB: $e")
        }

    }


    companion object {
        private val tableSchema = TableSchema.fromBean(FeesModel::class.java)
        private const val TABLE_NAME = "tbjw6003_txa_prod"
    }
}