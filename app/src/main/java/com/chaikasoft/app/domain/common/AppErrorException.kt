package com.chaikasoft.app.domain.common

class AppErrorException(val error: AppError) : Exception(error.toString())
