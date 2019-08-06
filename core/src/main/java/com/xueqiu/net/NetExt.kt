package com.xueqiu.net

const val REGEX_IP = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b"

fun String?.isIP(): Boolean {
    if (null == this) return false
    return this.matches(Regex(REGEX_IP))
}