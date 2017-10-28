package com.shun.service

import com.shun.commons.exception.ApiException
import com.shun.entity.UserInfo
import com.shun.feign.AppClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

/**
 * Created by Administrator on 2017/8/12.
 */
@Service
class AppService {

    @Autowired
    private lateinit var appClient: AppClient

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val logger by lazy { LoggerFactory.getLogger(AppService::class.java) }

    fun login(params: Map<String, String>): Any? {
        val session = getUserFromSession(appClient.login(params)["session"].toString()) ?: throw ApiException("无效的session")
        val temp = session.toString().split(":")
        logger.info("temp={}", temp)

        logger.info("last={}", temp[temp.size - 1])
        logger.info("size={}", temp.size)
        return mapOf("success" to true, "session" to appClient.login(params)["session"].toString())
    }

    private fun getUserFromSession(session: String): Any? = redisTemplate.keys(("cch:session:*:*:$session")).toTypedArray()

}