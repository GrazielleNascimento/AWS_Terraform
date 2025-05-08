output "lambda_function_name" {
  description = "Nome da função Lambda"
  value       = module.lambda_funcao_um.lambda_name
}

output "lambda_function_arn" {
  description = "ARN da função Lambda"
  value       = module.lambda_funcao_um.lambda_arn
}

output "lambda_invoke_arn" {
  description = "ARN de invocação da função Lambda"
  value       = module.lambda_funcao_um.lambda_invoke_arn
}

output "lambda_role_arn" {
  description = "ARN do IAM Role usado pela função Lambda"
  value       = var.create_role ? aws_iam_role.lambda_role[0].arn : var.lambda_role_arn
}