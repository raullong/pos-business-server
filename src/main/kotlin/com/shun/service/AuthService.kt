package com.shun.service

import com.shun.commons.exception.AppException
import com.shun.entity.User
import com.shun.entity.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
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
    private lateinit var mongoTemplate: MongoTemplate

    //用户登录
    fun login(username: String, password: String, type: Int): UserEntity {
        val user = mongoTemplate.findOne(Query.query(
                Criteria("username").`is`(username)
                        .and("status").`is`(1)), UserEntity::class.java) ?: throw AppException("用户不存在")

        if (user.type == null || !user.type!!.contains(1)) throw AppException("用户非管理员，不能登录")
        if (!passwordEncoder.matches(password, user.password)) throw AppException("密码错误")

        val token = UUID.randomUUID().toString()
        user.token = token
        mongoTemplate.save(user)

        return user
    }


    //获取登录用户信息
    fun info(username: String): User {
        val query = Query.query(Criteria("username").`is`(username))
        return mongoTemplate.findOne(query, UserEntity::class.java)
    }
}