package com.shun.service

import com.shun.commons.ApiUtils
import com.shun.commons.exception.AppException
import com.shun.entity.Ease
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

/**
 * Created by alwaysbe on 2017/11/27.
 *
 * @Email: lwn1207jak@163.com
 *
 * 环信平台对接
 */
@Service
class EaseService {

    private val logger by lazy { LoggerFactory.getLogger(EaseService::class.java) }

    @Value("\${ease.uri}")
    private lateinit var easeUrl: String

    @Value("\${ease.orgName}")
    private lateinit var easeOrgName: String

    @Value("\${ease.appName}")
    private lateinit var easeAppName: String

    @Value("\${ease.clientID}")
    private lateinit var easeClientID: String

    @Value("\${ease.clientSecret}")
    private lateinit var easeClientSecret: String

    @Autowired
    private lateinit var utils: ApiUtils

    @Autowired
    private lateinit var mongo: MongoTemplate


    /**
     * 获取token
     */
    fun accessToken(): Any {

        try {
            val resp = utils.post(mapOf(), mapOf(
                    "grant_type" to "client_credentials",
                    "client_id" to easeClientID,
                    "client_secret" to easeClientSecret
            ), "$easeUrl/$easeOrgName/$easeAppName/token", null, Map::class.java)

            return if (resp["access_token"] != null) resp["access_token"]!! else ""
        } catch (e: Exception) {
            throw AppException("环信平台获取token失败")
        }
    }

    /**
     * 注册用户
     */
    fun integrationUser(username: String, password: String): Ease {
        try {

            val accessToken = accessToken()

            val resp = utils.post(mapOf(), mapOf(
                    "username" to username,
                    "password" to password
            ), "$easeUrl/$easeOrgName/$easeAppName/users", mapOf("Authorization" to "Bearer $accessToken"), Map::class.java)


            val entities = resp["entities"] as List<*>
            if (entities.isNotEmpty()) {
                val entity = entities[0] as Map<*, *>

                val easeInfo = Ease()
                easeInfo.uuid = entity["uuid"]?.toString()
                easeInfo.type = entity["type"]?.toString()
                easeInfo.username = username
                easeInfo.activated = entity["activated"] as Boolean
                easeInfo.duration = resp["duration"]?.toString()?.toInt()
                easeInfo.organization = resp["organization"]?.toString()
                easeInfo.applicationName = resp["applicationName"]?.toString()

                return easeInfo
            } else {
                throw AppException("环信平台用户注册失败")
            }
        } catch (e: Exception) {
            throw AppException("环信平台用户注册失败")
        }
    }


    /**
     * 强制用户下线
     */
    fun disconnectUser(userUUID: String) {
        val ease = mongo.findOne(Query.query(Criteria("userUUID").`is`(userUUID)), Ease::class.java)

        if (ease != null) {
            val accessToken = accessToken()

            val resp = utils.get(
                    mapOf(),
                    "$easeUrl/$easeOrgName/$easeAppName/users/${ease.username}/disconnect",
                    mapOf("Authorization" to "Bearer $accessToken"),
                    Map::class.java
            )

            val data = resp["data"] as Map<*, *>
            if (data["result"].toString().toBoolean()) {
                logger.info("环信平台强制用户${ease.username}下线成功")
            } else {
                logger.info("环信平台强制用户${ease.username}下线失败")
            }

        }
    }
}