package com.shun.service

import com.shun.entity.*
import jodd.datetime.JDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service

/**
 * Created by rainbow on 2017/9/19.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Service
class SignService {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

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
     * @param position 用户的位置信息
     */
    fun aSign(user: User, position: Location) {
        val entity = SignEntity()

        entity.createUserUUID = user.uuid
        entity.date = JDateTime().toString("YYYY-MM-DD")
        entity.time = JDateTime().toString("hh:mm:ss")
        entity.position = position
        entity.status = 1

        mongoTemplate.insert(entity)

        mongoTemplate.updateFirst(Query.query(Criteria("uuid").`is`(user.uuid)), Update.update("position", position), UserEntity::class.java)
    }
}