package com.shun.service

import com.shun.commons.ApiUtils
import com.shun.commons.QueryUtils
import com.shun.commons.exception.AppException
import com.shun.entity.*
import jodd.datetime.JDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.NearQuery
import org.springframework.data.mongodb.core.query.Query
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

    @Autowired
    private lateinit var queryUtils: QueryUtils

    //创建用户
    fun create(user: User) {

        val entity = utils.copy(user, UserEntity::class.java)

        if (user.username.isNullOrEmpty()) throw AppException("用户名不能为空")
        if (user.mobile.isNullOrEmpty()) throw AppException("手机号不能为空")
        if (user.type == null || user.type!!.isEmpty()) throw AppException("用户类型不能为空")

        if (mongoTemplate.exists(Query.query(Criteria("username").`is`(user.username)), UserEntity::class.java)) throw AppException("用户${user.username}已存在")

        entity.uuid = UUID.randomUUID().toString()
        val md5Password = if (user.password.isNullOrEmpty()) utils.md5("123456") else utils.md5(user.password!!)
        entity.password = passwordEncoder.encode(md5Password)
        entity.createTime = Date()
        entity.nickname = user.nickname ?: user.username
        entity.status = 1
        entity.logicDel = 0
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

        val resp = mongoTemplate.find(query.with(Sort(Sort.Direction.DESC, "createTime")).skip((page - 1) * size).limit(size), UserEntity::class.java)
        val list = resp.map {
            val temp = utils.copy(it, UserResponse::class.java)
            temp.userType = mongoTemplate.find(Query.query(Criteria("key").`in`(it.type)), UserTypeEntity::class.java).joinToString { it.value!! }
            val position = mongoTemplate.findOne(Query.query(Criteria("userUUID").`is`(it.uuid)), UserPosition::class.java)
            if (position != null) {
                temp.address = position.address
                temp.position = position.position
            }
            temp
        }

        return Page(list, page, size, totalPage, totalSize)
    }

    // 重置密码
    fun resetPassword(uuid: String) {
        val user = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(uuid)), UserEntity::class.java)
        user.password = passwordEncoder.encode(utils.md5("123456"))
        mongoTemplate.save(user)
    }

    fun info(uuid: String) = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(uuid)), UserEntity::class.java) ?: throw AppException("用户错误")

    /**
     * 修改用户信息
     */
    fun save(user: User) {

        val entity = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(user.uuid)), UserEntity::class.java)

        val temp = mongoTemplate.findOne(Query.query(Criteria("username").`is`(user.username)), UserEntity::class.java)
        if (temp != null && temp.uuid != entity.uuid) throw AppException("用户${user.username}已存在")

        if (!user.mobile.isNullOrEmpty()) entity.mobile = user.mobile
        if (user.type != null && user.type!!.isNotEmpty()) entity.type = user.type
        if (!user.username.isNullOrEmpty()) entity.username = user.username
        if (!user.nickname.isNullOrEmpty()) entity.nickname = user.nickname
        if (user.status != null) entity.status = user.status
        if (user.superStar != null) entity.superStar = user.superStar

        mongoTemplate.save(entity)
    }


    fun remote(name: String): Any {
        return mongoTemplate.find(Query.query(Criteria().orOperator(
                Criteria("mobile").regex(name),
                Criteria("username").regex(name),
                Criteria("nickname").regex(name))), UserEntity::class.java)
    }


    fun getByToken(token: String): UserEntity {
        val user = mongoTemplate.findOne(Query.query(Criteria("token").`is`(token)), UserEntity::class.java) ?: throw AppException("USER-TOKEN 错误")
        if (user.status != 1) throw AppException("用户被禁用")

        return user
    }

    fun mapList(): List<UserResponse> {
        val resp = mongoTemplate.find(Query.query(Criteria("logicDel").`is`(0).and("status").`is`(1)), UserEntity::class.java)

        val list = mutableListOf<UserResponse>()

        resp.forEach {
            val temp = utils.copy(it, UserResponse::class.java)
            temp.userType = mongoTemplate.find(Query.query(Criteria("key").`in`(it.type)), UserTypeEntity::class.java).joinToString { it.value!! }
            val position = mongoTemplate.findOne(Query.query(Criteria("userUUID").`is`(it.uuid)), UserPosition::class.java)
            if (position != null) {
                temp.address = position.address
                temp.position = position.position

                list.add(temp)
            }
        }
        return list
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

        if (user.type == null || user.type!!.none { it != 1 }) throw AppException("用户类型错误")
        if (!passwordEncoder.matches(password, user.password)) throw AppException("用户名或密码错误")

        val token = UUID.randomUUID().toString()
        user.token = token
        mongoTemplate.save(user)

        return token
    }

    /**
     *
     */
    fun modifyPassword(mobile: String, code: String, password: String) {
        val user = mongoTemplate.findOne(Query.query(Criteria("mobile").`is`(mobile)), UserEntity::class.java) ?: throw AppException("用户不存在")

        // 验证code

        user.password = passwordEncoder.encode(password)
        mongoTemplate.save(user)
    }


    /**
     * 获取明星员工
     */
    fun superStar(): Any {
        val criteria = Criteria("status").`is`(1).and("superStar").`is`(1).and("logicDel").`is`(0)
        val query = queryUtils.buildQueryExclude(
                criteria,
                arrayListOf("id", "createTime", "lastTime", "password", "token", "type", "logicDel")
        )
        return mongoTemplate.find(query, UserEntity::class.java)
    }


    /**
     * 用户位置上传
     */
    fun aCollectGps(user: User, gps: Gps) {

        val uPosition = mongoTemplate.findOne(Query.query(Criteria("userUUID").`is`(user.uuid)), UserPosition::class.java) ?: UserPosition()

        val position = Position()
        val coordinate = gps.coordinate
        position.coordinates = arrayListOf(coordinate!!.lng!!, coordinate.lat!!)
        uPosition.position = position
        uPosition.address = gps.address
        uPosition.userUUID = user.uuid
        uPosition.createTime = Date()

        mongoTemplate.save(uPosition)

        val entity = utils.copy(gps, GpsEntity::class.java)

        entity.userUUID = user.uuid
        entity.createTime = JDateTime().toString("YYYY-MM-DD hh:mm:ss")
        entity.status = 1
        mongoTemplate.insert(entity)
    }


    /**
     * 获取用户某段时间的位置坐标数据
     */
    fun aGpsList(user: User, requestParams: Map<String, String>): Any {
        val beginTime = requestParams["beginTime"] ?: "${JDateTime().toString("YYYY-MM-DD")} 00:00:00"
        val endTime = requestParams["endTime"] ?: "${JDateTime().toString("YYYY-MM-DD")} 23:59:59"
        val page = requestParams["page"]
        val size = requestParams["size"]

        val criteria = Criteria("status").`is`(1).and("userUUID").`is`(user.uuid)

        criteria.andOperator(Criteria("createTime").gte(beginTime), Criteria("createTime").lte(endTime))

        if (!page.isNullOrEmpty() && !size.isNullOrEmpty()) {
            return queryUtils.queryObject(
                    criteria,
                    null,
                    arrayListOf("id", "status", "userUUID", "uuid"),
                    null,
                    arrayListOf("createTime"),
                    page.toString().toInt(),
                    size.toString().toInt(),
                    GpsEntity::class.java
            )
        } else {
            val query = queryUtils.buildQuery(
                    criteria,
                    null,
                    arrayListOf("id", "status", "userUUID", "uuid"),
                    null,
                    arrayListOf("createTime")
            )
            return mongoTemplate.find(query, GpsEntity::class.java)
        }
    }


    /**
     * 获取附近的同事
     */
    fun nearUser(user: User): Any {
        val uPosition = mongoTemplate.findOne(Query.query(Criteria("userUUID").`is`(user.uuid)), UserPosition::class.java)
        val position = uPosition.position
        if (position != null) {
            val coordinates = position.coordinates
            if (coordinates != null) {
                val point = Point(coordinates[0], coordinates[1])
                val radius = 2 / 111.0 // 半径2公里
                return mongoTemplate.find(Query.query(Criteria.where("position").within(Circle(point, radius))), UserEntity::class.java)
            }
        }
        return ""
    }


    /**
     * 通过坐标计算两用户之间的距离
     */
    fun queryDistance(user: User): Any {
        val uPosition = mongoTemplate.findOne(Query.query(Criteria("userUUID").`is`(user.uuid)), UserPosition::class.java)
        val position = uPosition.position
        val coordinates = position!!.coordinates
        val point = Point(coordinates!![0], coordinates[1])

        val criteria = Criteria("userUUID").ne(user.uuid)
        val query = Query(criteria)
        query.fields().exclude("id").exclude("createTime")
        val nearQuery = NearQuery.near(point, Metrics.KILOMETERS).query(query)
        val geoResults = mongoTemplate.geoNear(nearQuery, UserPosition::class.java)

        return geoResults.content.map {
            val item = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(it.content.userUUID)), UserEntity::class.java)
            mapOf(
                    "userInfo" to mapOf(
                            "uuid" to item.uuid,
                            "username" to item.username,
                            "mobile" to item.mobile,
                            "nickname" to item.nickname,
                            "position" to it.content.position
                    ),
                    "distance" to Formatter().format("%.2f", it.distance.value).toString()
            )
        }
    }

    /**
     * 获取用户详情
     */
    fun aUserInfo(uuid: String): Any {
        val criteria = Criteria("uuid").`is`(uuid)

        val query = queryUtils.buildQueryExclude(
                criteria,
                arrayListOf("id", "status", "type", "password", "createTime", "lastTime", "token", "logicDel")
        )
        return mongoTemplate.findOne(query, UserEntity::class.java)
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