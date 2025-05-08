package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.regions.Region;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FuncaoDoisHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName = System.getenv("DYNAMODB_TABLE_NAME");

    public FuncaoDoisHandler() {
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.of(System.getenv("LAMBDA_AWS_REGION")))
                .build();
    }

    // Para testes
    public FuncaoDoisHandler(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        try {
            context.getLogger().log("Input: " + input);

            // Extrair valores do evento
            String name = (String) input.get("name");
            String date = (String) input.get("date");

            if (name == null || date == null) {
                return createResponse(400, "Parâmetros obrigatórios: name e date");
            }

            // Gerar chaves
            String pk = "LIST#" + date.replace("-", "");
            String itemId = UUID.randomUUID().toString();
            String sk = "ITEM#" + itemId;
            String createdAt = Instant.now().toString();

            // Criar item
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("PK", AttributeValue.builder().s(pk).build());
            item.put("SK", AttributeValue.builder().s(sk).build());
            item.put("name", AttributeValue.builder().s(name).build());
            item.put("status", AttributeValue.builder().s("TODO").build());
            item.put("createdAt", AttributeValue.builder().s(createdAt).build());

            // Salvar no DynamoDB
            PutItemRequest request = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build();

            dynamoDbClient.putItem(request);

            // Preparar resposta
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("PK", pk);
            responseBody.put("SK", sk);
            responseBody.put("name", name);
            responseBody.put("status", "TODO");
            responseBody.put("createdAt", createdAt);

            return createResponse(201, responseBody);

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            return createResponse(500, "Error creating item: " + e.getMessage());
        }
    }

    private Map<String, Object> createResponse(int statusCode, Object body) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("body", body);
        return response;
    }
}