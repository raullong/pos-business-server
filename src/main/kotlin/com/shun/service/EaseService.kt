package com.shun.service

import com.shun.commons.ApiUtils
import com.shun.commons.exception.AppException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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


    /**
     * 获取token
     */
    fun accessToken(): Any {

        try {
            val resp = utils.post(mapOf(), mapOf(
                    "grant_type" to "client_credentials",
                    "client_id" to easeClientID,
                    "client_secret" to easeClientSecret
            ), "$easeUrl/$easeOrgName/$easeAppName/token", Map::class.java)

            return if (resp["access_token"] != null) resp["access_token"]!! else ""
        } catch (e: Exception) {
            throw AppException("环信平台获取token失")
        }
    }
}