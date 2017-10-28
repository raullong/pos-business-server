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
 * Created by Administrator on 2017/8/13.
 */
@Service
class DispatchService {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var utils: ApiUtils

    fun create(user: User, dispatch: Dispatch) {

        val entity = utils.copy(dispatch, DispatchEntity::class.java)

        val merchantCriteria = Criteria()
        if (!dispatch.merchantName.isNullOrEmpty()) merchantCriteria.and("name").`is`(dispatch.merchantName)
        if (!dispatch.merchantCode.isNullOrEmpty()) merchantCriteria.and("code").`is`(dispatch.merchantCode)
        val merchant = mongoTemplate.findOne(Query.query(merchantCriteria), MerchantEntity::class.java)

        if (!dispatch.machineCode.isNullOrEmpty()) {
            entity.machineCode = dispatch.machineCode
            mongoTemplate.updateFirst(Query.query(merchantCriteria), Update().set("machineCode", dispatch.machineCode), MerchantEntity::class.java)
        }
        if (!dispatch.machineCode.isNullOrEmpty()) {
            entity.machineCode = dispatch.machineCode
            mongoTemplate.updateFirst(Query.query(merchantCriteria), Update().set("machineCode", dispatch.machineCode), MerchantEntity::class.java)
        } else {
            if (dispatch.machineCode.isNullOrEmpty()) entity.machineCode = merchant.machineCode
        }

        entity.merchantUUID = merchant.uuid

        if (!dispatch.signUserMobile.isNullOrEmpty()) {
            val signUser = mongoTemplate.findOne(Query.query(Criteria("mobile").`is`(dispatch.signUserMobile)), User::class.java)
            entity.signUserName = signUser.username
        }

        if (!dispatch.installUserMobile.isNullOrEmpty()) {
            val installUser = mongoTemplate.findOne(Query.query(Criteria("mobile").`is`(dispatch.installUserMobile)), User::class.java)
            entity.installUserName = installUser.username
        }

        if (!dispatch.drawUserMobile.isNullOrEmpty()) {
            val drawUser = mongoTemplate.findOne(Query.query(Criteria("mobile").`is`(dispatch.drawUserMobile)), User::class.java)
            entity.drawUserName = drawUser.username
        }

        entity.uuid = UUID.randomUUID().toString()
        entity.createUserUUID = user.uuid
        entity.createTime = Date()

        mongoTemplate.insert(entity)
    }

    fun list(params: Map<String, String?>): Any {
        val criteria = Criteria()

        val orList = mutableListOf<Criteria>()
        if (!params["searchKey"].isNullOrEmpty()) {
            orList.add(Criteria("merchantName").regex(params["searchKey"]))
            orList.add(Criteria("merchantCode").regex(params["searchKey"]))
            orList.add(Criteria("machineCode").regex(params["searchKey"]))
            orList.add(Criteria("type").`is`(params["searchKey"]))
            orList.add(Criteria("signUserMobile").regex(params["searchKey"]))
            orList.add(Criteria("signUserName").regex(params["searchKey"]))
            orList.add(Criteria("drawUserName").regex(params["searchKey"]))
            orList.add(Criteria("drawUserMobile").regex(params["searchKey"]))
            orList.add(Criteria("installUserName").regex(params["searchKey"]))
            orList.add(Criteria("installUserMobile").regex(params["searchKey"]))
        }

        if (!params["status"].isNullOrEmpty()) criteria.and("status").`in`(params["status"]!!.split(",").map(String::toInt))

        if (orList.isNotEmpty()) criteria.andOperator(Criteria().orOperator(*orList.toTypedArray()))
        val query = Query.query(criteria)

        val page = if (params["page"] != null) params["page"].toString().toInt() else 1
        val size = if (params["size"] != null) params["size"].toString().toInt() else 10

        val totalSize = mongoTemplate.count(query, DispatchEntity::class.java)
        val totalPage = Math.ceil((totalSize / size.toDouble())).toInt()

        val resp = mongoTemplate.find(query.skip((page - 1) * size).limit(size), DispatchEntity::class.java)

        val list = resp.map {
            val item = utils.copy(it, DispatchResponse::class.java)
            item.createUser = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(it.createUserUUID)), User::class.java)
            item.merchant = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(it.merchantUUID)), MerchantEntity::class.java)
            item
        }

        return Page(list, page, size, totalPage, totalSize)
    }


    fun info(uuid: String): DispatchEntity {
        return mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(uuid)), DispatchEntity::class.java)
    }

    fun save(dispatch: Dispatch) {
        val entity = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(dispatch.uuid)), DispatchEntity::class.java)

        val merchantCriteria = Criteria()
        if (!dispatch.merchantName.isNullOrEmpty()) merchantCriteria.and("name").`is`(dispatch.merchantName)
        if (!dispatch.merchantCode.isNullOrEmpty()) merchantCriteria.and("code").`is`(dispatch.merchantCode)
        val merchant = mongoTemplate.findOne(Query.query(merchantCriteria), MerchantEntity::class.java)

        if (!dispatch.machineCode.isNullOrEmpty()) {
            entity.machineCode = dispatch.machineCode
            mongoTemplate.updateFirst(Query.query(merchantCriteria), Update().set("machineCode", dispatch.machineCode), MerchantEntity::class.java)
        }

        entity.merchantUUID = merchant.uuid
        entity.merchantName = merchant.name
        entity.merchantCode = merchant.code
        if (!dispatch.type.isNullOrEmpty()) entity.type = dispatch.type
        if (dispatch.money != null) entity.money = dispatch.money
        if (!dispatch.moneyType.isNullOrEmpty()) entity.moneyType = dispatch.moneyType
        if (!dispatch.type.isNullOrEmpty()) entity.type = dispatch.type

        if (!dispatch.signUserMobile.isNullOrEmpty()) {
            val signUser = mongoTemplate.findOne(Query.query(Criteria("mobile").`is`(dispatch.signUserMobile)), User::class.java)
            entity.signUserName = signUser.username
            entity.signUserMobile = signUser.mobile
        }

        if (!dispatch.installUserMobile.isNullOrEmpty()) {
            val installUser = mongoTemplate.findOne(Query.query(Criteria("mobile").`is`(dispatch.installUserMobile)), User::class.java)
            entity.installUserName = installUser.username
            entity.installUserMobile = installUser.mobile
        }

        if (!dispatch.drawUserMobile.isNullOrEmpty()) {
            val drawUser = mongoTemplate.findOne(Query.query(Criteria("mobile").`is`(dispatch.drawUserMobile)), User::class.java)
            entity.drawUserName = drawUser.username
            entity.drawUserMobile = drawUser.mobile
        }

        if (!dispatch.installTime.isNullOrEmpty()) entity.installTime = dispatch.installTime
        if (!dispatch.remark.isNullOrEmpty()) entity.remark = dispatch.remark
        if (dispatch.status != null) entity.status = dispatch.status
        if (dispatch.messageMoney != null) entity.messageMoney = dispatch.messageMoney

        mongoTemplate.save(entity)
    }

    fun delete(uuid: String) {
        mongoTemplate.remove(Query.query(Criteria("uuid").`is`(uuid)), DispatchEntity::class.java)
    }
}