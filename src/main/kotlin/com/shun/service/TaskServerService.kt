package com.shun.service

import com.shun.commons.ApiUtils
import com.shun.entity.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import java.util.*

/**
 * Created by rainbow on 2017/9/19.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Service
class TaskServerService {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var utils: ApiUtils

    fun create(user: User, taskServer: TaskServer) {

        val temp = utils.copy(taskServer, TaskServerEntity::class.java)

        val criteria = Criteria()
        if (!taskServer.merchantName.isNullOrEmpty()) criteria.and("name").`is`(taskServer.merchantName)
        if (!taskServer.merchantCode.isNullOrEmpty()) criteria.and("code").`is`(taskServer.merchantCode)
        val merchant = mongoTemplate.findOne(Query.query(criteria), MerchantEntity::class.java)

        temp.uuid = UUID.randomUUID().toString()
        temp.merchantUUID = merchant.uuid
        temp.machineCode = merchant.machineCode
        temp.merchantName = merchant.name
        temp.merchantCode = merchant.code
        temp.status = temp.status ?: 0
        temp.createTime = Date()
        temp.createUserUUID = user.uuid

        mongoTemplate.insert(temp)
    }

    fun list(params: Map<String, String>): Any {
        val criteria = Criteria()

        val orList = mutableListOf<Criteria>()
        if (!params["searchKey"].isNullOrEmpty()) {
            orList.add(Criteria("merchantName").regex(params["searchKey"]))
            orList.add(Criteria("merchantCode").regex(params["searchKey"]))
            orList.add(Criteria("machineCode").regex(params["searchKey"]))
            orList.add(Criteria("serverName").regex(params["searchKey"]))
            orList.add(Criteria("serverMobile").regex(params["searchKey"]))
            orList.add(Criteria("type").regex(params["searchKey"]))
        }

        if (!params["status"].isNullOrEmpty()) criteria.and("status").`in`(params["status"]!!.split(",").map(String::toInt))

        if (orList.isNotEmpty()) criteria.andOperator(Criteria().orOperator(*orList.toTypedArray()))
        val query = Query.query(criteria)

        val page = if (params["page"] != null) params["page"].toString().toInt() else 1
        val size = if (params["size"] != null) params["size"].toString().toInt() else 10

        val totalSize = mongoTemplate.count(query, TaskServerEntity::class.java)
        val totalPage = Math.ceil((totalSize / size.toDouble())).toInt()

        val resp = mongoTemplate.find(query.skip((page - 1) * size).limit(size), TaskServerEntity::class.java)

        val list = resp.map {
            val item = utils.copy(it, TaskServerResponse::class.java)
            item.merchant = mongoTemplate.findOne(Query.query(Criteria("code").`is`(it.merchantCode)), MerchantEntity::class.java)
            item.createUser = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(it.createUserUUID)), User::class.java)
            item
        }

        return Page(list, page, size, totalPage, totalSize)
    }


    fun info(uuid: String): TaskServerEntity {
        return mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(uuid)), TaskServerEntity::class.java)
    }

    fun save(taskServer: TaskServer) {
        val entity = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(taskServer.uuid)), TaskServerEntity::class.java)
        val merchantCriteria = Criteria()
        if (!taskServer.merchantName.isNullOrEmpty()) merchantCriteria.and("name").`is`(taskServer.merchantName)
        if (!taskServer.merchantCode.isNullOrEmpty()) merchantCriteria.and("code").`is`(taskServer.merchantCode)
        val merchant = mongoTemplate.findOne(Query.query(merchantCriteria), MerchantEntity::class.java)

        entity.merchantUUID = merchant.uuid
        entity.merchantName = merchant.name
        entity.merchantCode = merchant.code
        entity.machineCode = merchant.machineCode

        if (!taskServer.question.isNullOrEmpty()) entity.question = taskServer.question

        if (!taskServer.serverMobile.isNullOrEmpty()) {
            val serverUser = mongoTemplate.findOne(Query.query(Criteria("mobile").`is`(taskServer.serverMobile)), User::class.java)
            entity.serverName = serverUser.username
            entity.serverMobile = serverUser.mobile
        }

        if (!taskServer.taskTime.isNullOrEmpty()) entity.taskTime = taskServer.taskTime
        if (!taskServer.issuseTime.isNullOrEmpty()) entity.issuseTime = taskServer.issuseTime
        if (!taskServer.type.isNullOrEmpty()) entity.type = taskServer.type
        if (taskServer.status != null) entity.status = taskServer.status

        mongoTemplate.save(entity)
    }
}