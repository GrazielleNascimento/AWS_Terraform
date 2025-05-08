resource "aws_iam_role" "delete_item_lambda_role" {
  name = "delete-item-lambda-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_policy" "dynamodb_delete_policy" {
  name        = "dynamodb-delete-policy"
  description = "Policy to allow Lambda to delete items from DynamoDB"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "dynamodb:GetItem",
          "dynamodb:DeleteItem"
        ]
        Effect   = "Allow"
        Resource = aws_dynamodb_table.todo_list_table.arn
      },
      {
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Effect   = "Allow"
        Resource = "arn:aws:logs:*:*:*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "delete_item_lambda_policy_attachment" {
  role       = aws_iam_role.delete_item_lambda_role.name
  policy_arn = aws_iam_policy.dynamodb_delete_policy.arn
}

resource "aws_lambda_function" "delete_item_lambda" {
  function_name = "delete-item-lambda"
  role          = aws_iam_role.delete_item_lambda_role.arn
  handler       = "com.example.FuncaoQuatroHandler::handleRequest"
  runtime       = "java21"

  filename      = "../lambda/funcao-quatro/target/funcao-quatro-1.0-SNAPSHOT.jar"

  memory_size   = 512
  timeout       = 15

  source_code_hash = filebase64sha256("../lambda/funcao-quatro/target/funcao-quatro-1.0-SNAPSHOT.jar")

  environment {
    variables = {
      DYNAMODB_TABLE_NAME = aws_dynamodb_table.todo_list_table.name
      LAMBDA_AWS_REGION   = var.region
    }
  }
}