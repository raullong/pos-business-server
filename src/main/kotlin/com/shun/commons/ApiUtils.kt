package com.shun.commons

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Component
import org.springframework.util.DigestUtils
import java.text.SimpleDateFormat

/**
 * Created by rainbow on 2017/6/15.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Component
class ApiUtils {


    fun <T> copy(source: Any, target: Class<T>): T {
        val instance = target.newInstance()
        BeanUtils.copyProperties(source, instance)
        return instance
    }

    val mapper by lazy {
        val mapper = jacksonObjectMapper()
        mapper.dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        mapper
    }

    fun md5(str: String): String {
        return DigestUtils.md5DigestAsHex(str.toByteArray())!!
    }
}