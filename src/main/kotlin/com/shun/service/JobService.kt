package com.shun.service

import com.shun.commons.exception.AppException
import com.shun.entity.DispatchEntity
import com.shun.entity.MerchantEntity
import com.shun.entity.TaskServerEntity
import com.shun.entity.User
import jodd.datetime.JDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

/**
 * Created by alwaysbe on 2017/11/6.
 *
 * @Email: lwn1207jak@163.com
 */
@Service
class JobService {

    @Autowired
    private lateinit var mongo: MongoTemplate

    @Autowired
    private lateinit var userService: UserService

    /**
     * APP端相关应用接口
     */

    /**
     * 获取今日任务列表
     */
    fun list(user: User): Any {

        val task = mongo.find(Query.query(Criteria("logicDel").`is`(0)
                .and("serverUserUUID").`is`(user.uuid)
                .and("taskTime").regex(JDateTime().toString("YYYY-MM-DD"))), TaskServerEntity::class.java)

        val dispatch = mongo.find(Query.query(Criteria("logicDel").`is`(0)
                .and("installUserUUID").`is`(user.uuid)
                .and("installTime").regex(JDateTime().toString("YYYY-MM-DD"))), DispatchEntity::class.java)

        val resp = mutableListOf<Any>()
        resp.addAll(task.map {
            mapOf(
                    "uuid" to it.uuid,
                    "type" to it.type,
                    "machineCode" to it.machineCode,
                    "createUser" to userService.findByUUID(it.createUserUUID!!),
                    "createTime" to it.createTime,
                    "kind" to "task"
            )
        })
        resp.addAll(dispatch.map {
            mapOf(
                    "uuid" to it.uuid,
                    "type" to it.type,
                    "machineCode" to it.machineCode,
                    "createUser" to userService.findByUUID(it.createUserUUID!!),
                    "createTime" to it.createTime,
                    "kind" to "dispatch"
            )
        })

        return resp
    }


    /**
     * 获取任务详情
     */
    fun info(uuid: String, kind: String): Any {
        when (kind) {
            "task" -> {
                val task = mongo.findOne(Query.query(Criteria("uuid").`is`(uuid)), TaskServerEntity::class.java)

                val merchantQuery = Query.query(Criteria("uuid").`is`(task.merchantUUID))
                merchantQuery.fields().include("name").include("code").include("address").include("locationInfo")
                        .include("linkerMobile").include("linkerName").include("machineCode")
                val merchant = mongo.findOne(merchantQuery, MerchantEntity::class.java)

                return mapOf(
                        "task" to mapOf(
                                "uuid" to task.uuid,
                                "type" to task.type,
                                "remark" to task.question,
                                "machineCode" to merchant.machineCode,
                                "kind" to "task"
                        ),
                        "merchant" to merchant
                )
            }
            "dispatch" -> {
                val task = mongo.findOne(Query.query(Criteria("uuid").`is`(uuid)), DispatchEntity::class.java)

                val merchantQuery = Query.query(Criteria("uuid").`is`(task.merchantUUID))
                merchantQuery.fields().include("name").include("code").include("address").include("locationInfo")
                        .include("linkerMobile").include("linkerName").include("machineCode")
                val merchant = mongo.findOne(merchantQuery, MerchantEntity::class.java)

                return mapOf(
                        "task" to mapOf(
                                "uuid" to task.uuid,
                                "type" to task.type,
                                "remark" to task.remark,
                                "machineCode" to merchant.machineCode,
                                "kind" to "task"
                        ),
                        "merchant" to merchant
                )
            }
            else -> throw AppException("获取任务详情失败")
        }
    }


}