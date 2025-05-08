variable "function_name" {
  description = "Nome da função Lambda"
  type        = string
}

variable "handler" {
  description = "Handler da função Lambda"
  type        = string
}

variable "runtime" {
  description = "Runtime da função Lambda"
  type        = string
}

variable "role_arn" {
  description = "ARN do IAM Role para a função Lambda"
  type        = string
}

variable "memory_size" {
  description = "Tamanho da memória alocada para a função Lambda (MB)"
  type        = number
  default     = 512
}

variable "timeout" {
  description = "Timeout da função Lambda (segundos)"
  type        = number
  default     = 10
}