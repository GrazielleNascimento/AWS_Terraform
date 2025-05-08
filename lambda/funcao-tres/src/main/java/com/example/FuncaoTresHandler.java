package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

public class FuncaoTresHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName = System.getenv("DYNAMODB_TABLE_NAME");

    public FuncaoTresHandler() {
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.of(System.getenv("LAMBDA_AWS_REGION")))
                .build();
    }

    // Para testes
    public FuncaoTresHandler(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        try {
            context.getLogger().log("Input: " + event);

            // Extrair valores do evento
            String listId = (String) event.get("listId");
            String itemId = (String) event.get("itemId");
            Map<String, Object> updates = (Map<String, Object>) event.get("updates");

            if (listId == null || itemId == null || updates == null || updates.isEmpty()) {
                return createResponse(400, "Parâmetros obrigatórios: listId, itemId e updates");
            }

            // Verificar se o item existe
            if (!itemExists(listId, itemId)) {
                return createResponse(404, "Item não encontrado");
            }

            // Atualizar o item
            Map<String, Object> updatedItem = updateItem(listId, itemId, updates);

            // Preparar resposta
            return createResponse(200, updatedItem);

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            return createResponse(500, "Erro ao atualizar item: " + e.getMessage());
        }
    }

    private boolean itemExists(String listId, String itemId) {
        try {
            GetItemRequest request = GetItemRequest.builder()
                    .tableName(tableName)
                    .key(Map.of(
                            "PK", AttributeValue.builder().s("LIST#" + listId).build(),
                            "SK", AttributeValue.builder().s("ITEM#" + itemId).build()
                    ))
                    .build();

            GetItemResponse response = dynamoDbClient.getItem(request);
            return response.hasItem();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar existência do item: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> updateItem(String listId, String itemId, Map<String, Object> updates) {
        try {
            // Construir expressão de atualização
            StringBuilder updateExpression = new StringBuilder("SET");
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            Map<String, String> expressionAttributeNames = new HashMap<>();

            int i = 0;
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                String attributeName = entry.getKey();
                Object attributeValue = entry.getValue();

                String placeholder = ":val" + i;
                String attrName = "#attr" + i;

                updateExpression.append(" ").append(attrName).append(" = ").append(placeholder).append(",");
                expressionAttributeValues.put(placeholder, toAttributeValue(attributeValue));
                expressionAttributeNames.put(attrName, attributeName);
                i++;
            }

            // Remover a vírgula final
            String finalUpdateExpression = updateExpression.substring(0, updateExpression.length() - 1);

            // Executar atualização
            UpdateItemRequest request = UpdateItemRequest.builder()
                    .tableName(tableName)
                    .key(Map.of(
                            "PK", AttributeValue.builder().s("LIST#" + listId).build(),
                            "SK", AttributeValue.builder().s("ITEM#" + itemId).build()
                    ))
                    .updateExpression(finalUpdateExpression)
                    .expressionAttributeValues(expressionAttributeValues)
                    .expressionAttributeNames(expressionAttributeNames)
                    .returnValues(ReturnValue.ALL_NEW)
                    .build();

            UpdateItemResponse response = dynamoDbClient.updateItem(request);

            // Converter resposta para mapa
            return fromAttributeValueMap(response.attributes());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar item: " + e.getMessage(), e);
        }
    }

    private AttributeValue toAttributeValue(Object value) {
        if (value instanceof String) {
            return AttributeValue.builder().s((String) value).build();
        } else if (value instanceof Number) {
            return AttributeValue.builder().n(value.toString()).build();
        } else if (value instanceof Boolean) {
            return AttributeValue.builder().bool((Boolean) value).build();
        } else {
            // Para outros tipos, convertemos para string
            return AttributeValue.builder().s(value.toString()).build();
        }
    }

    private Map<String, Object> fromAttributeValueMap(Map<String, AttributeValue> attributeMap) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, AttributeValue> entry : attributeMap.entrySet()) {
            AttributeValue attrValue = entry.getValue();

            if (attrValue.s() != null) {
                result.put(entry.getKey(), attrValue.s());
            } else if (attrValue.n() != null) {
                result.put(entry.getKey(), Double.parseDouble(attrValue.n()));
            } else if (attrValue.bool() != null) {
                result.put(entry.getKey(), attrValue.bool());
            }
            // Adicione mais conversões conforme necessário
        }

        return result;
    }

    private Map<String, Object> createResponse(int statusCode, Object body) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("body", body);
        return response;
    }
}