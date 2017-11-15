package com.shun.service

import com.shun.entity.Gps
import com.shun.entity.Sign
import com.shun.entity.SignEntity
import com.shun.entity.User
import jodd.datetime.JDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service

/**
 * Created by rainbow on 2017/9/19.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Service
class SignService {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var userService: UserService

    fun create(params: Map<String, Any>) {

    }

    fun list() = mongoTemplate.findAll(Sign::class.java)


    /**
     * APP端相关应用接口
     */

    /**
     * 用户签到
     *
     * @param user 登录用户
     * @param gps 用户的位置信息
     */
    fun aSign(user: User, gps: Gps) {
        val entity = SignEntity()

        entity.createUserUUID = user.uuid
        entity.date = JDateTime().toString("YYYY-MM-DD")
        entity.time = JDateTime().toString("hh:mm:ss")
        entity.position = gps.coordinate
        entity.address = gps.address
        entity.status = 1

        mongoTemplate.insert(entity)

        userService.aCollectGps(user, gps)
    }
}