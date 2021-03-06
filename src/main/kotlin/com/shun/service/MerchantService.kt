package com.shun.service

import com.shun.commons.ApiUtils
import com.shun.commons.QueryUtils
import com.shun.entity.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import java.util.*

/**
 * Created by alwaysbe on 2017/10/26.
 *
 * @Email: lwn1207jak@163.com
 */
@Service
class MerchantService {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var queryUtils: QueryUtils

    @Autowired
    private lateinit var utils: ApiUtils

    fun list(params: Map<String, String?>): Any {
        val criteria = Criteria("logicDel").`is`(0)

        val orList = mutableListOf<Criteria>()
        if (!params["searchKey"].isNullOrEmpty()) {
            orList.add(Criteria("name").regex(params["searchKey"]))
            orList.add(Criteria("code").regex(params["searchKey"]))
            orList.add(Criteria("linkerMobile").regex(params["searchKey"]))
            orList.add(Criteria("linkerName").regex(params["searchKey"]))
            orList.add(Criteria("machineCode").regex(params["searchKey"]))
            orList.add(Criteria("address").regex(params["searchKey"]))
        }

        if (!params["status"].isNullOrEmpty()) criteria.and("status").`in`(params["status"]!!.split(",").map(String::toInt))

        if (orList.isNotEmpty()) criteria.andOperator(Criteria().orOperator(*orList.toTypedArray()))
        val query = Query.query(criteria)

        val page = if (params["page"] != null) params["page"].toString().toInt() else 1
        val size = if (params["size"] != null) params["size"].toString().toInt() else 10

        val totalSize = mongoTemplate.count(query, MerchantEntity::class.java)
        val totalPage = Math.ceil((totalSize / size.toDouble())).toInt()

        val resp = mongoTemplate.find(query.skip((page - 1) * size).limit(size), MerchantEntity::class.java)

        val list = resp.map {
            val item = utils.copy(it, MerchantResponse::class.java)
            item.createUser = userService.findByUUID(it.createUserUUID)
            item
        }

        return Page(list, page, size, totalPage, totalSize)
    }

    fun create(user: User, merchant: Merchant) {
        val entity = utils.copy(merchant, MerchantEntity::class.java)

        entity.uuid = UUID.randomUUID().toString()
        entity.createUserUUID = user.uuid
        entity.createTime = Date()
        entity.logicDel = 0
        entity.status = merchant.status ?: 1

        mongoTemplate.insert(entity)
    }


    fun info(uuid: String): MerchantEntity {
        return mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(uuid)), MerchantEntity::class.java)
    }


    fun save(merchant: Merchant) {

        val item = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(merchant.uuid)), MerchantEntity::class.java)

        if (!merchant.name.isNullOrEmpty()) item.name = merchant.name
        if (!merchant.code.isNullOrEmpty()) item.code = merchant.code
        if (merchant.locationInfo != null) item.locationInfo = merchant.locationInfo
        if (!merchant.address.isNullOrEmpty()) item.address = merchant.address
        if (merchant.status != null) item.status = merchant.status
        if (!merchant.remark.isNullOrEmpty()) item.remark = merchant.remark
        if (merchant.images != null && merchant.images!!.isNotEmpty()) item.images = merchant.images
        if (!merchant.linkerMobile.isNullOrEmpty()) item.linkerMobile = merchant.linkerMobile
        if (!merchant.linkerName.isNullOrEmpty()) item.linkerName = merchant.linkerName
        if (!merchant.machineCode.isNullOrEmpty()) item.machineCode = merchant.machineCode

        mongoTemplate.save(item)
    }

    fun delete(uuid: String) {
        mongoTemplate.updateFirst(Query.query(Criteria("uuid").`is`(uuid)), Update.update("logicDel", 1), MerchantEntity::class.java)
    }

    fun remote(name: String): Any {
        return mongoTemplate.find(Query.query(Criteria("logicDel").`is`(0).and("name").regex(name)), MerchantEntity::class.java)
    }

    fun mapList(): List<MerchantEntity> {
        return mongoTemplate.find(Query.query(Criteria("logicDel").`is`(0).and("status").`is`(1)), MerchantEntity::class.java)
    }


    /**
     * APP端相关应用接口
     */

    /**
     * 商户位置采集
     */
    fun aCollectLocation(uuid: String, gps: Gps) {

        val merchantQuery = queryUtils.buildQuery(Criteria("uuid").`is`(uuid))
        val merchant = mongoTemplate.findOne(merchantQuery, MerchantEntity::class.java)
        if (merchant != null) {
            val position = Position()
            val coordinate = gps.coordinate
            position.coordinates = arrayListOf(coordinate!!.lng!!, coordinate.lat!!)
            merchant.locationInfo = position
            merchant.address = gps.address
        }
        mongoTemplate.save(merchant)
    }
}