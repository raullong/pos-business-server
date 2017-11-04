package com.shun.service

import com.shun.commons.ApiUtils
import com.shun.entity.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
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
class NoteService {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var utils: ApiUtils


    fun create(request: Note, user: User) {
        requireNotNull(request.title, { "标题不允许为空" })
        requireNotNull(request.content, { "内容不允许为空" })
        requireNotNull(user.uuid, { "用户错误" })

        val note = NoteEntity()

        note.uuid = UUID.randomUUID().toString()
        note.content = request.content
        note.title = request.title
        note.createTime = Date()
        note.createUserUUID = user.uuid
        note.type = if (request.type != null && request.type != "") {
            request.type
        } else "通知"
        note.urgency = request.urgency
        note.status = 1
        note.isDel = 0

        mongoTemplate.insert(note)

    }

    fun list(params: Map<String, String?>): Any {
        val criteria = Criteria("isDel").`is`(0)

        val orList = mutableListOf<Criteria>()
        if (!params["searchKey"].isNullOrEmpty()) {
            orList.add(Criteria("title").regex(params["searchKey"]))
            orList.add(Criteria("content").regex(params["searchKey"]))
        }

        if (!params["urgency"].isNullOrEmpty()) criteria.and("urgency").`in`(params["urgency"]!!.split(",").map(String::toInt))

        if (orList.isNotEmpty()) criteria.andOperator(Criteria().orOperator(*orList.toTypedArray()))
        val query = Query.query(criteria)

        val page = if (params["page"] != null) params["page"].toString().toInt() else 1
        val size = if (params["size"] != null) params["size"].toString().toInt() else 10

        val totalSize = mongoTemplate.count(query, NoteEntity::class.java)
        val totalPage = Math.ceil((totalSize / size.toDouble())).toInt()

        val resp = mongoTemplate.find(query.with(Sort(Sort.Direction.DESC, "createTime")).skip((page - 1) * size).limit(size), NoteEntity::class.java)

        val list = resp.map {
            val temp = utils.copy(it, NoteResponse::class.java)

            val userQuery = Query.query(Criteria("uuid").`is`(it.createUserUUID))
            userQuery.fields().include("username").include("mobile").include("nickname").include("uuid").exclude("id")
            temp.createUser = mongoTemplate.findOne(userQuery, UserEntity::class.java)
            temp
        }

        return Page(list, page, size, totalPage, totalSize)
    }


    fun info(uuid: String): Note {
        return mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(uuid)), NoteEntity::class.java)
    }

    fun save(note: Note) {
        val entity = mongoTemplate.findOne(Query.query(Criteria("uuid").`is`(note.uuid)), NoteEntity::class.java)

        if (entity != null) {
            entity.title = note.title
            entity.content = note.content
            entity.type = note.type
            entity.urgency = note.urgency

            mongoTemplate.save(entity)
        }
    }

    fun delete(uuid: String) {
        mongoTemplate.updateFirst(Query.query(Criteria("uuid").`is`(uuid)), Update.update("isDel", 1), NoteEntity::class.java)
    }


    /**
     * APP端相关应用接口
     */

    /**
     * 获取紧急通知信息列表
     */
    fun urgencyNoteList(params: Map<String, String?>): Any {
        val criteria = Criteria("isDel").`is`(0).and("urgency").`is`(1)

        val query = Query.query(criteria)
        query.fields().exclude("id").exclude("isDel")

        val page = if (params["page"] != null) params["page"].toString().toInt() else 1
        val size = if (params["size"] != null) params["size"].toString().toInt() else 10

        val totalSize = mongoTemplate.count(query, Note::class.java)
        val totalPage = Math.ceil((totalSize / size.toDouble())).toInt()

        val resp = mongoTemplate.find(query.with(Sort(Sort.Direction.DESC, "createTime")).skip((page - 1) * size).limit(size), NoteEntity::class.java)

        val list = resp.map {
            val temp = utils.copy(it, NoteResponse::class.java)

            val userQuery = Query.query(Criteria("uuid").`is`(it.createUserUUID))
            userQuery.fields().include("username").include("mobile").include("nickname").include("uuid").exclude("id")
            temp.createUser = mongoTemplate.findOne(userQuery, UserEntity::class.java)
            temp
        }

        return Page(list, page, size, totalPage, totalSize)
    }
}