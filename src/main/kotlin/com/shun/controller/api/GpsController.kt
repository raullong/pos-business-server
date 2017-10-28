package com.shun.controller.api

import com.shun.commons.exception.ApiException
import com.shun.entity.GpsEntity
import com.shun.entity.User
import jodd.datetime.JDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Created by Administrator on 2017/8/9.
 */
@RestController
@RequestMapping("/api/v1/gps")
class GpsController {


    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @PostMapping("/collect")
    fun collect(@RequestBody gps: GpsEntity, @RequestParam mobile: String) {
        val user = mongoTemplate.findOne(Query.query(Criteria("mobile").`is`(mobile)), User::class.java) ?: throw ApiException("用户异常")

        gps.userID = user.uuid
        mongoTemplate.insert(gps)

        mongoTemplate.updateFirst(
                Query.query(Criteria("uuid").`is`(gps.userID)),
                Update.update("lastTime", Date()).set("latitude", gps.latitude).set("longitude", gps.longitude),
                User::class.java
        )

    }

    @GetMapping("/list")
    fun list() = mongoTemplate.findAll(User::class.java)
}