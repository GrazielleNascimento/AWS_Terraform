resource "aws_iam_role" "add_item_lambda_role" {
  name = "add-item-lambda-role"

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

resource "aws_iam_policy" "dynamodb_write_policy" {
  name        = "dynamodb-write-policy"
  description = "Policy to allow Lambda to write to DynamoDB"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "dynamodb:PutItem"
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

resource "aws_iam_role_policy_attachment" "add_item_lambda_policy_attachment" {
  role       = aws_iam_role.add_item_lambda_role.name
  policy_arn = aws_iam_policy.dynamodb_write_policy.arn
}

resource "aws_lambda_function" "add_item_lambda" {
  function_name = "add-item-lambda"
  role          = aws_iam_role.add_item_lambda_role.arn
  handler       = "com.example.FuncaoDoisHandler::handleRequest"
  runtime       = "java21"

  filename      = "../lambda/funcao-dois/target/funcao-dois-1.0-SNAPSHOT.jar"

  memory_size   = 512
  timeout       = 15

  source_code_hash = filebase64sha256("../lambda/funcao-dois/target/funcao-dois-1.0-SNAPSHOT.jar")

  environment {
    variables = {
      DYNAMODB_TABLE_NAME = aws_dynamodb_table.todo_list_table.name
      LAMBDA_AWS_REGION          = var.region
    }
  }
}

# Definir a tabela DynamoDB (se ela não existir no seu Terraform)
resource "aws_dynamodb_table" "todo_list_table" {
  name         = "TodoList"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "PK"
  range_key    = "SK"

  attribute {
    name = "PK"
    type = "S"
  }

  attribute {
    name = "SK"
    type = "S"
  }

  tags = {
    Environment = "dev"
    Project     = "TodoList"
  }
}