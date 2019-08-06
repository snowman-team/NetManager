package com.xueqiu.net

class EmptyResponse

abstract class BaseErrorResponse {

    abstract fun getErrorCode(): Int?

    abstract fun getErrorMsg(): String?

}