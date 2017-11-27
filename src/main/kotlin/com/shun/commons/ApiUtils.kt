package com.shun.commons

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.BeanUtils
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.util.DigestUtils
import java.net.URI
import java.text.SimpleDateFormat

/**
 * Created by rainbow on 2017/6/15.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Component
class ApiUtils {

    val restTemplate by lazy {
        RestTemplateBuilder().additionalMessageConverters(
                StringHttpMessageConverter(Charsets.UTF_8),
                MappingJackson2HttpMessageConverter()
        ).build()!!
    }


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


    fun <T> post(urlParams: Map<String, Any?>, params: Map<String, Any?>, url: String, headers: Map<String, String>?, responseType: Class<T>): T {
        val header = HttpHeaders()
        headers?.forEach {
            header.set(it.key, it.value)
        }
        val entity = genEntity(params, header, HttpMethod.POST, buildUri(url, urlParams))
        return restTemplate.exchange(entity, responseType).body
    }

    fun buildUri(url: String, params: Map<String, Any?> = emptyMap()): String {
        val query = params.filterValues { it != null }.map { "${it.key}=${it.value}" }.joinToString("&")
        val sep = if (url.contains("?")) "&" else "?"
        return "$url$sep$query"
    }


    private fun <T> genEntity(body: T, headers: HttpHeaders, method: HttpMethod, url: String): RequestEntity<T> {
        return RequestEntity(body, headers, method, URI(url))
    }

}