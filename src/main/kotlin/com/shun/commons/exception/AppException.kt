package com.shun.commons.exception

import org.springframework.web.HttpRequestMethodNotSupportedException
import java.lang.reflect.UndeclaredThrowableException

/**
 * Created by alwaysbe on 2017/10/28.
 * 
 * @Email: lwn1207jak@163.com
 */
open class AppException(override val message: String) : Throwable(message) {

    companion object {
        fun parse(ex: Throwable): AppException {
            if (ex is AppException) return ex
            if (ex is IllegalArgumentException) return AppException("参数不正确[${ex.message}]")
            if (ex is HttpRequestMethodNotSupportedException) return AppException("HTTP请求方法错误[${ex.message}]")
            if (ex is UndeclaredThrowableException) return parse(ex.undeclaredThrowable)
            if (ex.cause != null) return parse(ex.cause!!)
            ex.printStackTrace()
            return AppException("未知错误[${ex.message}]")
        }
    }
}