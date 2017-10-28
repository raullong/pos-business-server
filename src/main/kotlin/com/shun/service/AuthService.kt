package com.shun.service

import com.shun.commons.ApiUtils
import com.shun.commons.exception.AppException
import com.shun.entity.User
import com.shun.entity.UserEntity
import com.shun.entity.UserInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

/**
 * Created by rainbow on 2017/8/9.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Service
class AuthService {

    @Autowired
    private lateinit var passwordEncoder: BCryptPasswordEncoder
    @Autowired
    private lateinit var utils: ApiUtils

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    //用户登录
    fun login(mobile: String, password: String, type: Int): UserEntity {
        val user = mongoTemplate.findOne(Query.query(
                Criteria("mobile").`is`(mobile)
                        .and("type").`is`(type)
                        .and("status").`is`(1)), UserEntity::class.java) ?: throw AppException("用户不存在")

        if (!passwordEncoder.matches(password, user.password)) throw AppException("密码错误")

        val token = UUID.randomUUID().toString()
        user.token = token
        mongoTemplate.save(user)

        return user
    }


    //获取登录用户信息
    fun info(mobile: String): User {
        val query = Query.query(Criteria("mobile").`is`(mobile))
        return mongoTemplate.findOne(query, User::class.java)
    }


    private fun getUserFromSession(session: String): UserInfo? {
        val keys = redisTemplate.keys(("cch:session:*:*:$session")).toTypedArray()
        if (keys.size != 1) return null
        val info = redisTemplate.opsForValue().get(keys[0]) ?: return null
        return utils.mapper.readValue(info, UserInfo::class.java)
    }

    private fun exists(mobile: String) = mongoTemplate.exists(Query.query(Criteria("mobile").`is`(mobile)), User::class.java)
}