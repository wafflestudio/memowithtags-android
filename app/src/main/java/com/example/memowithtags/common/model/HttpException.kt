package com.example.memowithtags.common.model

class HttpException(val statusCode: Int, message: String) : Exception(message)
