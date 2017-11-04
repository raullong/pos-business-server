package com.shun.commons

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.shun.config.BeetlProperties
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import org.beetl.core.Configuration
import org.beetl.core.GroupTemplate
import org.beetl.core.resource.StringTemplateResourceLoader
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.util.DigestUtils
import java.text.SimpleDateFormat

/**
 * Created by rainbow on 2017/6/15.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Component
open class ApiUtils {

    @Autowired
    lateinit private var properties: BeetlProperties

    fun <T> copy(source: Any, target: Class<T>): T {
        val instance = target.newInstance()
        BeanUtils.copyProperties(source, instance)
        return instance
    }

    val restTemplate by lazy {
        RestTemplateBuilder().additionalMessageConverters(
                StringHttpMessageConverter(Charsets.UTF_8),
                MappingJackson2HttpMessageConverter()
        ).build()!!
    }

    val mapper by lazy {
        val mapper = jacksonObjectMapper()
        mapper.dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        mapper
    }

    val objectMapper by lazy { ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)!! }

    fun buildUri(url: String, params: Map<String, Any?> = emptyMap()): String {
        val query = params.filterValues { it != null }.map { "${it.key}=${it.value}" }.joinToString("&")
        val sep = if (url.contains("?")) "&" else "?"
        return "$url$sep$query"
    }

    fun <T> mapToBean(map: Map<*, *>, clazz: Class<T>) = mapper.readValue(mapper.writeValueAsString(map), clazz)!!


    private val groupTemplate by lazy {
        val loader = StringTemplateResourceLoader()
        val cfg = Configuration.defaultConfiguration()
        cfg.placeholderStart = properties.properties["DELIMITER_PLACEHOLDER_START"].toString()
        GroupTemplate(loader, cfg)
    }

    fun render(src: String, data: Map<String, Any?>): String {
        val template = groupTemplate.getTemplate(src)
        template.binding(data)
        return template.render()
    }


    private val pyFormat by lazy {
        val format = HanyuPinyinOutputFormat()
        format.caseType = HanyuPinyinCaseType.LOWERCASE
        format.toneType = HanyuPinyinToneType.WITHOUT_TONE
        format.vCharType = HanyuPinyinVCharType.WITH_V
        format
    }

    fun stringToPinyin(src: String, type: String) = src.map {
        try {
            val s = PinyinHelper.toHanyuPinyinStringArray(it, pyFormat)[0]
            when (type) {
                "full" -> s
                "first" -> s.substring(0, 1)
                else -> ""
            }
        } catch (e: Exception) {
            it.toString()
        }
    }.joinToString("")

    fun md5(str: String): String {
        return DigestUtils.md5DigestAsHex(str.toByteArray())!!
    }
}