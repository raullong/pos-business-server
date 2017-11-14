package com.shun.service

import com.shun.commons.ApiUtils
import com.shun.commons.QueryUtils
import com.shun.commons.exception.AppException
import com.shun.entity.*
import jodd.datetime.JDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Created by rainbow on 2017/9/19.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Service
class TaskService {

    @Value("\${image.path}")
    lateinit private var imagePath: String

    @Value("\${image.baseUrl}")
    lateinit private var imageBaseUrl: String

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var utils: ApiUtils

    @Autowired
    private lateinit var queryUtils: QueryUtils

    /**
     * 创建维护工单
     */
    fun create(user: User, kind: String, task: Task) {

        val temp = utils.copy(task, TaskEntity::class.java)

        val criteria = Criteria()
        if (!task.merchantName.isNullOrEmpty()) criteria.and("name").`is`(task.merchantName)
        val merchant = mongoTemplate.findOne(Query.query(criteria), MerchantEntity::class.java)

        temp.uuid = UUID.randomUUID().toString()
        temp.merchantUUID = merchant.uuid
        temp.status = temp.status ?: 0
        temp.logicDel = 0
        temp.kind = kind
        temp.createTime = Date()
        temp.createUserUUID = user.uuid

        if (!task.serverMobile.isNullOrEmpty()) {
            temp.serverUserUUID = userService.findByMobile(task.serverMobile!!).uuid
        }
        if (!task.signUserMobile.isNullOrEmpty()) {
            val signUser = userService.findByMobile(task.signUserMobile!!)
            temp.signUserUUID = signUser.uuid
        }

        if (!task.installUserMobile.isNullOrEmpty()) {
            temp.installUserUUID = userService.findByMobile(task.installUserMobile!!).uuid
        }

        if (!task.drawUserMobile.isNullOrEmpty()) {
            temp.drawUserUUID = userService.findByMobile(task.drawUserMobile!!).uuid
        }

        mongoTemplate.insert(temp)
    }

    fun list(kind: String, params: Map<String, String>): Any {
        val criteria = Criteria("logicDel").`is`(0).and("kind").`is`(kind)

        val orList = mutableListOf<Criteria>()
        if (!params["searchKey"].isNullOrEmpty()) {
            val merchant = mongoTemplate.find(Query.query(Criteria().orOperator(*arrayListOf(
                    Criteria("name").regex(params["searchKey"]),
                    Criteria("code").regex(params["searchKey"]),
                    Criteria("machineCode").regex(params["searchKey"])
            ).toTypedArray())), MerchantEntity::class.java)
            orList.add(Criteria("merchantUUID").`in`(merchant.map { it.uuid }))

            val user = mongoTemplate.find(Query.query(Criteria().orOperator(*arrayListOf(
                    Criteria("mobile").regex(params["searchKey"]),
                    Criteria("username").regex(params["searchKey"]),
                    Criteria("nickname").regex(params["searchKey"])
            ).toTypedArray())), UserEntity::class.java)

            orList.add(Criteria("signUserUUID").`in`(user.map { it.uuid }))
            orList.add(Criteria("drawUserUUID").`in`(user.map { it.uuid }))
            orList.add(Criteria("installUserUUID").`in`(user.map { it.uuid }))
            orList.add(Criteria("serverUserUUID").`in`(user.map { it.uuid }))
            orList.add(Criteria("createUserUUID").`in`(user.map { it.uuid }))
            orList.add(Criteria("type").regex(params["searchKey"]))
        }

        if (!params["status"].isNullOrEmpty()) criteria.and("status").`in`(params["status"]!!.split(",").map(String::toInt))

        if (orList.isNotEmpty()) criteria.andOperator(Criteria().orOperator(*orList.toTypedArray()))
        val query = Query.query(criteria)

        val page = if (params["page"] != null) params["page"].toString().toInt() else 1
        val size = if (params["size"] != null) params["size"].toString().toInt() else 10

        val totalSize = mongoTemplate.count(query, TaskEntity::class.java)
        val totalPage = Math.ceil((totalSize / size.toDouble())).toInt()

        val resp = mongoTemplate.find(query.with(Sort(Sort.Direction.DESC, "createTime")).skip((page - 1) * size).limit(size), TaskEntity::class.java)

        val list = resp.map {
            val item = utils.copy(it, TaskResponse::class.java)
            item.merchant = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(it.merchantUUID)), MerchantEntity::class.java)
            item.signUser = userService.findByUUID(it.signUserUUID)
            item.drawUser = userService.findByUUID(it.drawUserUUID)
            item.installUser = userService.findByUUID(it.installUserUUID)
            item.serverUser = userService.findByUUID(it.serverUserUUID)
            item.createUser = userService.findByUUID(it.createUserUUID)
            item
        }

        return Page(list, page, size, totalPage, totalSize)
    }


    fun info(uuid: String): TaskEntity {
        return mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(uuid)), TaskEntity::class.java)
    }

    fun save(task: Task) {
        val entity = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(task.uuid)), TaskEntity::class.java)
        val merchantCriteria = Criteria()
        if (!task.merchantName.isNullOrEmpty()) merchantCriteria.and("name").`is`(task.merchantName)
        val merchant = mongoTemplate.findOne(Query.query(merchantCriteria), MerchantEntity::class.java)

        entity.merchantUUID = merchant.uuid
        entity.merchantName = merchant.name

        if (!task.remark.isNullOrEmpty()) entity.remark = task.remark

        if (!task.signUserMobile.isNullOrEmpty()) {
            val signUser = userService.findByMobile(task.signUserMobile!!)
            entity.signUserUUID = signUser.uuid
            entity.signUserMobile = signUser.mobile
        }

        if (!task.installUserMobile.isNullOrEmpty()) {
            val installUser = userService.findByMobile(task.installUserMobile!!)
            entity.installUserUUID = installUser.uuid
            entity.installUserMobile = installUser.mobile
        }

        if (!task.drawUserMobile.isNullOrEmpty()) {
            val drawUser = userService.findByMobile(task.drawUserMobile!!)
            entity.drawUserUUID = drawUser.uuid
            entity.drawUserMobile = drawUser.mobile
        }

        if (!task.serverMobile.isNullOrEmpty()) {
            val serverUser = userService.findByMobile(task.serverMobile!!)
            entity.serverUserUUID = serverUser.uuid
            entity.serverMobile = serverUser.mobile
        }

        if (!task.serverTime.isNullOrEmpty()) entity.serverTime = task.serverTime
        if (!task.issueTime.isNullOrEmpty()) entity.issueTime = task.issueTime
        if (!task.type.isNullOrEmpty()) entity.type = task.type
        if (task.status != null) entity.status = task.status

        mongoTemplate.save(entity)
    }


    fun delete(uuid: String) {
        mongoTemplate.updateFirst(Query.query(Criteria("uuid").`is`(uuid)), Update.update("logicDel", 1), TaskEntity::class.java)
    }


    /**
     * APP端相关应用接口
     */

    /**
     * 获取今日任务列表
     */
    fun curTask(user: User, page: Int, size: Int): Any {
        val criteria = Criteria("logicDel").`is`(0)

        val timeOrOperator = mutableListOf<Criteria>()
        timeOrOperator.add(Criteria("installTime").regex(JDateTime().toString("YYYY-MM-DD")))
        timeOrOperator.add(Criteria("serverTime").regex(JDateTime().toString("YYYY-MM-DD")))

        val userOrOperator = mutableListOf<Criteria>()
        userOrOperator.add(Criteria("installUserUUID").`is`(user.uuid))
        userOrOperator.add(Criteria("serverUserUUID").`is`(user.uuid))

        criteria.andOperator(Criteria().orOperator(*timeOrOperator.toTypedArray()), Criteria().orOperator(*userOrOperator.toTypedArray()))

        val pageInfo = queryUtils.queryObject(
                criteria,
                arrayListOf("uuid", "type", "status", "createTime", "createUserUUID", "merchantUUID"),
                arrayListOf("id"),
                arrayListOf("createTime"),
                null,
                page,
                size,
                TaskEntity::class.java)

        val list = pageInfo.list.map {
            val item = utils.copy(it, TaskResponse::class.java)

            val merchantQuery = queryUtils.buildQuery(
                    Criteria("uuid").`is`(it.merchantUUID),
                    arrayListOf("machineCode"),
                    arrayListOf("id")
            )

            val userQuery = queryUtils.buildQuery(
                    Criteria("uuid").`is`(it.createUserUUID),
                    arrayListOf("nickname", "username"),
                    arrayListOf("id")
            )
            item.merchant = mongoTemplate.findOne(merchantQuery, MerchantEntity::class.java)
            item.createUser = mongoTemplate.findOne(userQuery, UserEntity::class.java)
            item
        }

        return Page(list, page, size, pageInfo.totalPage, pageInfo.totalSize)
    }


    /**
     * 获取任务详情
     */
    fun aTaskInfo(uuid: String): Any {

        val taskQuery = queryUtils.buildQueryInclude(
                Criteria("uuid").`is`(uuid),
                arrayListOf("uuid", "type", "remark", "status", "money", "moneyType", "messageMoney", "messageMoneyType", "merchantUUID")
        )
        val info = mongoTemplate.findOne(taskQuery, TaskEntity::class.java)
        val item = utils.copy(info, TaskResponse::class.java)
        val merchantQuery = queryUtils.buildQueryExclude(
                Criteria("uuid").`is`(info.merchantUUID),
                arrayListOf("id", "status", "remark", "logicDel", "createUserUUID")
        )
        item.merchant = mongoTemplate.findOne(merchantQuery, MerchantEntity::class.java)
        return item
    }


    /**
     * 做任务
     *
     * @param user              操作用户
     * @param uuid              任务uuid
     * @param items             维护项目
     * @param messageMoney      通讯费
     * @param messageMoneyType  通讯费类型
     * @param money             机具押金
     * @param moneyType         机具押金类型
     * @param remark            任务补充说明
     * @param images            相关图片
     */
    fun doTask(user: User,
               uuid: String,
               items: String?,
               messageMoney: Double?,
               messageMoneyType: String?,
               money: Double?,
               moneyType: String?,
               remark: String?,
               images: List<MultipartFile>?
    ) {
        val taskQuery = queryUtils.buildQuery(Criteria("uuid").`is`(uuid))
        val info = mongoTemplate.findOne(taskQuery, TaskEntity::class.java) ?: throw AppException("数据有误")

        if (!items.isNullOrEmpty()) info.items = items!!.split(",")
        if (messageMoney != null && messageMoney > 0) info.messageMoney = messageMoney
        if (!messageMoneyType.isNullOrEmpty()) info.messageMoneyType = messageMoneyType
        if (money != null && money > 0) info.money = money
        if (!moneyType.isNullOrEmpty()) info.moneyType = moneyType
        if (!remark.isNullOrEmpty()) info.remark = "${info.remark} $remark"
        if (images != null && images.isNotEmpty()) {
            info.images = images.map {
                val fileName = it.originalFilename
                val suffix = fileName.substringAfterLast(".")
                val currentTimeMillis = System.currentTimeMillis()
                val imageFile = File("$imagePath$currentTimeMillis.$suffix")

                val outStream = FileOutputStream(imageFile)
                outStream.write(it.bytes)
                outStream.close()
                "$imageBaseUrl$imagePath$currentTimeMillis.$suffix"
            }
        }
        mongoTemplate.save(info)
    }
}