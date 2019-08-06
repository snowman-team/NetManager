package com.xueqiu.net.app

import com.google.gson.annotations.SerializedName
import com.xueqiu.net.BaseErrorResponse

class ErrorResponse : BaseErrorResponse() {

    @SerializedName("error_code")
    var code: String? = null

    @SerializedName("error_description")
    var desc: String? = null

    override fun getErrorCode(): Int? = code?.toInt()

    override fun getErrorMsg(): String? = desc
}