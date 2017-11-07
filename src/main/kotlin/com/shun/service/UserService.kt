package com.shun.service

import com.shun.commons.ApiUtils
import com.shun.commons.exception.AppException
import com.shun.entity.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

/**
 * Created by Administrator on 2017/8/12.
 */
@Service
class UserService {

    @Autowired
    private lateinit var passwordEncoder: BCryptPasswordEncoder

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var utils: ApiUtils

    //创建用户
    fun create(user: User) {

        val entity = utils.copy(user, UserEntity::class.java)

        if (user.mobile.isNullOrEmpty()) throw AppException("手机号不能为空")
        if (user.type == null) throw AppException("用户类型不能为空")

        if (mongoTemplate.exists(Query.query(Criteria("mobile").`is`(user.mobile).and("type").`is`(user.type)), UserEntity::class.java)) throw AppException("用户已存在")

        entity.uuid = UUID.randomUUID().toString()
        val md5Password = if (user.password.isNullOrEmpty()) utils.md5("123456") else utils.md5(user.password!!)
        entity.password = passwordEncoder.encode(md5Password)
        entity.createTime = Date()
        entity.status = 1
        entity.superStar = 0

        mongoTemplate.insert(entity)

    }

    fun types(): List<UserTypeEntity> {
        return mongoTemplate.find(Query.query(Criteria("status").`is`(1)), UserTypeEntity::class.java)
    }

    fun list(params: Map<String, String>): Any {
        val criteria = Criteria()

        val orList = mutableListOf<Criteria>()
        if (!params["searchKey"].isNullOrEmpty()) {
            orList.add(Criteria("username").regex(params["searchKey"]))
            orList.add(Criteria("mobile").regex(params["searchKey"]))
        }

        if (!params["status"].isNullOrEmpty()) criteria.and("status").`in`(params["status"]!!.split(",").map(String::toInt))
        if (!params["type"].isNullOrEmpty()) criteria.and("type").`in`(params["type"]!!.split(",").map(String::toInt))
        if (!params["superStar"].isNullOrEmpty()) criteria.and("superStar").`in`(params["superStar"]!!.split(",").map(String::toInt))

        if (orList.isNotEmpty()) criteria.andOperator(Criteria().orOperator(*orList.toTypedArray()))
        val query = Query.query(criteria)

        val page = if (params["page"] != null) params["page"].toString().toInt() else 1
        val size = if (params["size"] != null) params["size"].toString().toInt() else 10

        val totalSize = mongoTemplate.count(query, UserEntity::class.java)
        val totalPage = Math.ceil((totalSize / size.toDouble())).toInt()

        val resp = mongoTemplate.find(query.skip((page - 1) * size).limit(size), UserEntity::class.java)
        val list = resp.map {
            val temp = utils.copy(it, UserResponse::class.java)
            temp.userType = mongoTemplate.findOne(Query.query(Criteria("key").`is`(it.type)), UserTypeEntity::class.java)
            temp
        }

        return Page(list, page, size, totalPage, totalSize)
    }

    // 重置密码
    fun resetPassword(uuid: String) {
        val user = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(uuid)), UserEntity::class.java)
        user.password = passwordEncoder.encode("123456")
        mongoTemplate.save(user)
    }

    fun info(uuid: String) = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(uuid)), UserEntity::class.java) ?: throw AppException("用户错误")


    fun save(user: User) {

        val entity = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(user.uuid)), UserEntity::class.java)

        val type = user.type ?: entity.type
        val mobile = user.mobile ?: entity.mobile

        val temp = mongoTemplate.findOne(Query.query(Criteria("mobile").`is`(mobile).and("type").`is`(type)), UserEntity::class.java)
        if (temp != null && temp.uuid != entity.uuid) throw AppException("用户已存在")

        entity.mobile = mobile
        entity.type = type
        entity.username = user.username
        entity.nickname = user.nickname
        entity.status = user.status
        entity.superStar = user.superStar

        mongoTemplate.save(entity)
    }


    fun modify(mobile: String, params: Map<String, Any?>) {
        params["status"] ?: throw AppException("参数错误")
        mongoTemplate.findAndModify(Query.query(Criteria("mobile").`is`(mobile)), Update().set("status", params["status"].toString().toInt()), User::class.java)
    }


    fun remote(mobile: String): Any {
        return mongoTemplate.find(Query.query(Criteria("mobile").regex(mobile)), User::class.java)
    }


    fun getByToken(token: String): UserEntity {
        val user = mongoTemplate.findOne(Query.query(Criteria("token").`is`(token)), UserEntity::class.java) ?: throw AppException("USER-TOKEN 错误")
        if (user.status != 1) throw AppException("用户被禁用")

        return user
    }


    /**
     * APP端相关应用接口
     */

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码(需md5加密)
     *
     * @return String  用户token
     */
    fun appLogin(username: String, password: String): String {
        val user = mongoTemplate.findOne(Query.query(Criteria("username").`is`(username).and("status").`is`(1)), UserEntity::class.java) ?: throw AppException("用户不存在")

        if (!passwordEncoder.matches(password, user.password)) throw AppException("用户名或密码错误")

        val token = UUID.randomUUID().toString()
        user.token = token
        mongoTemplate.save(user)

        return token
    }

    /**
     * 获取明星员工
     */
    fun superStar(): Any {
        val query = Query.query(Criteria("status").`is`(1).and("superStar").`is`(1))
        query.fields().exclude("id")
                .exclude("createTime")
                .exclude("lastTime")
                .exclude("password")
                .exclude("token")
                .exclude("type")
        return mongoTemplate.find(query, UserEntity::class.java)
    }


    /**
     * 公共方法
     */

    fun findByUUID(uuid: String?): User? {
        return if (!uuid.isNullOrEmpty()) {
            val query = Query.query(Criteria("uuid").`is`(uuid))
            query.fields().include("uuid").include("username").include("mobile").include("nickname").include("position").exclude("id")
            mongoTemplate.findOne(query, UserEntity::class.java)
        } else null
    }

    fun findByMobile(mobile: String): User {
        val query = Query.query(Criteria("mobile").`is`(mobile))
        query.fields().include("uuid").include("username").include("mobile").include("nickname").include("position").exclude("id")
        return mongoTemplate.findOne(query, UserEntity::class.java)
    }
}