package com.shun.controller.api

import com.shun.commons.exception.ApiException
import com.shun.entity.GpsEntity
import com.shun.entity.NewPosition
import com.shun.entity.User
import com.shun.entity.UserInfo
import com.shun.service.AppService
import jodd.datetime.JDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpSession

/**
 * Created by Administrator on 2017/8/12.
 */
@RestController
@RequestMapping("/api/v1/app")
class AppController {

    @Autowired
    private lateinit var appService: AppService

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @PostMapping("/login")
    fun login(@RequestBody map: Map<String, String>) = appService.login(map)

    //收集GPS坐标信息
    @PostMapping("/collect")
    fun collect(@RequestBody data: GpsEntity, @SessionAttribute("user") user: User) {
        data.userID = user.uuid
        data.alias = user.username
        // 历史位置
        mongoTemplate.insert(data)

        val position = NewPosition()
        position.lat = data.latitude
        position.lng = data.longitude
        val oldPosition = mongoTemplate.findOne(Query.query(Criteria("mobile").`is`(user.mobile)), UserInfo::class.java)

        var oldLng = 0.00
        var oldLat = 0.00
        if (oldPosition == null) {
            oldLng = 0.00
            oldLat = 0.00
        } else {
            oldLng = oldPosition.position!!.lng!!
            oldLat = oldPosition.position!!.lat!!
        }

        position.address = data.address
        position.mobile = user.mobile
        // 最新位置
        mongoTemplate.upsert(
                Query.query(Criteria("userID").`is`(data.userID)),
                Update.update("lastTime", Date()).set("position", position).set("mobile", user.mobile).set("username", user.username),
                UserInfo::class.java)
    }

    // 司机的最新位置(一分钟内)
    @GetMapping
    fun last(): List<UserInfo>? = mongoTemplate.find(Query.query(Criteria.where("lastTime").gte(JDateTime().addMinute(-1).convertToDate())), UserInfo::class.java)

    // 某司机某一时间段的坐标位置
    @GetMapping("/spell")
    fun spell(@RequestParam mobile: String,
              @RequestParam(required = false) begin: String? = null,
              @RequestParam(required = false) end: String? = null): List<GpsEntity>? {

        // 查询userUUID
        val userID = mongoTemplate.findOne(Query.query(Criteria.where("mobile").`is`(mobile)), UserInfo::class.java).uuid

        // 如果参数为空，默认查询一小时内的gps信息
        if (begin == null && end == null) return mongoTemplate.find(Query.query(Criteria.where("userID").`is`(userID)
                .and("timestamp").gte(JDateTime().addHour(-1).convertToDate())).with(Sort(Sort.Direction.DESC, "_id")),
                GpsEntity::class.java)

        // 筛选begin到end时间段的gps信息
        return mongoTemplate.find(Query.query(Criteria.where("userID").`is`(userID)
                .and("timestamp").gte(JDateTime(begin).convertToDate())
                .lte(JDateTime(end).convertToDate())), GpsEntity::class.java)
    }


    @GetMapping("/last")
    fun appGps(): Any? {
        val map = mutableMapOf<String, Any>()
        try {
            map.put("success", true)
            map.put("list", mongoTemplate.find(Query.query(Criteria.where("lastTime").gte(JDateTime().addMinute(-1).convertToDate())), UserInfo::class.java))
        } catch (e: ApiException) {
            map.put("success", false)
            map.put("message", e.message!!)
        }
        return map
    }
}
