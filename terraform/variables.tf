variable "region" {
  type        = string
  description = "Região da AWS"
  default     = "us-east-1"
}

variable "profile" {
  type        = string
  description = "Perfil AWS CLI"
  default     = "default"
}

variable "function_name" {
  type        = string
  description = "Nome da função Lambda"
  default     = "funcao-um"
}

variable "handler" {
  type        = string
  description = "Handler da função Lambda"
  default     = "com.example.FuncaoUmHandler::handleRequest"
}

variable "runtime" {
  type        = string
  description = "Runtime da função Lambda"
  default     = "java21"
}

variable "memory_size" {
  type        = number
  description = "Tamanho da memória alocada para a função Lambda (MB)"
  default     = 512
}

variable "timeout" {
  type        = number
  description = "Timeout da função Lambda (segundos)"
  default     = 10
}

variable "lambda_role_arn" {
  type        = string
  description = "ARN do IAM Role para a função Lambda"
  default     = ""
}

variable "create_role" {
  type        = bool
  description = "Criar um novo IAM Role para a função Lambda?"
  default     = true
}