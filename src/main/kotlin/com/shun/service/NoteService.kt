package com.shun.service

import com.shun.commons.ApiUtils
import com.shun.entity.*
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
 * Created by Administrator on 2017/8/13.
 */
@Service
class NoteService {

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
        note.status = request.status ?: 1
        note.logicDel = 0

        mongoTemplate.insert(note)

    }

    fun list(params: Map<String, String?>): Any {
        val criteria = Criteria("logicDel").`is`(0)

        val orList = mutableListOf<Criteria>()
        if (!params["searchKey"].isNullOrEmpty()) {
            orList.add(Criteria("title").regex(params["searchKey"]))
            orList.add(Criteria("content").regex(params["searchKey"]))
        }

        if (!params["urgency"].isNullOrEmpty()) criteria.and("urgency").`in`(params["urgency"]!!.split(",").map(String::toInt))

        if (orList.isNotEmpty()) criteria.andOperator(Criteria().orOperator(*orList.toTypedArray()))
        val query = Query.query(criteria)

        return queryPage(query, params)
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
        mongoTemplate.updateFirst(Query.query(Criteria("uuid").`is`(uuid)), Update.update("logicDel", 1), NoteEntity::class.java)
    }


    /**
     * APP端相关应用接口
     */

    /**
     * 获取紧急通知信息列表
     */
    fun urgencyNoteList(params: Map<String, String?>): Any {
        val criteria = Criteria("logicDel").`is`(0).and("urgency").`is`(1)

        val query = Query.query(criteria)
        query.fields().exclude("id").exclude("logicDel")

        return queryPage(query, params)
    }

    /**
     * 获取通知公告列表，分已读和未读。
     */
    fun appNoteList(user: User, params: Map<String, String?>): Any {
        val criteria = Criteria("logicDel").`is`(0)

        val isRead = (params["isRead"] ?: 1).toString().toInt()

        val readNoteUUID = mongoTemplate.findOne(Query.query(Criteria("userUUID").`is`(user.uuid)), NoteReaded::class.java)
        when (isRead) {
            1 -> criteria.and("uuid").`in`(readNoteUUID.noteUUID)
            else -> criteria.and("uuid").nin(readNoteUUID.noteUUID)
        }

        val query = Query.query(criteria)
        query.fields().exclude("id").exclude("isDel")

        return queryPage(query, params)
    }


    /**
     * 通知公告详情，标记为已读。
     *
     * @param user 登录用户
     * @param uuid 公告uuid
     */
    fun appNoteInfo(user: User, uuid: String): Any {
        val criteria = Criteria("uuid").`is`(uuid)
        val query = Query.query(criteria)
        query.fields().exclude("id").exclude("logicDel")

        val note = mongoTemplate.findOne(query, NoteEntity::class.java)

        if (note != null) {
            val readNote = mongoTemplate.findOne(Query.query(Criteria("userUUID").`is`(user.uuid)), NoteReaded::class.java)

            if (readNote != null) {
                readNote.noteUUID = readNote.noteUUID!!.plus(uuid)
                mongoTemplate.save(readNote)
            } else {
                val noteEntity = NoteReaded()
                noteEntity.userUUID = user.uuid
                noteEntity.noteUUID = arrayListOf(uuid)
                noteEntity.createTime = Date()
                noteEntity.status = 1

                mongoTemplate.insert(noteEntity)
            }
        }
        return note
    }


    /**
     * 发布公告
     *
     * @param user 登录用户
     * @param title 公告标题
     * @param content 公告内容
     * @param urgency 是否紧急
     * @param images 公告图片
     */
    fun appCreateNote(user: User, title: String, content: String, urgency: Int, images: List<MultipartFile>?) {
        val entity = NoteEntity()
        entity.title = title
        entity.content = content
        entity.logicDel = 0
        entity.status = 1
        entity.urgency = urgency

        entity.images = images?.map {
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


    private fun queryPage(query: Query, params: Map<String, String?>): Any {
        val page = if (params["page"] != null) params["page"].toString().toInt() else 1
        val size = if (params["size"] != null) params["size"].toString().toInt() else 10

        val totalSize = mongoTemplate.count(query, Note::class.java)
        val totalPage = Math.ceil((totalSize / size.toDouble())).toInt()

        val resp = mongoTemplate.find(query.with(Sort(Sort.Direction.DESC, "createTime")).skip((page - 1) * size).limit(size), NoteEntity::class.java)

        val list = resp.map {
            val temp = utils.copy(it, NoteResponse::class.java)
            temp.createUser = userService.findByUUID(it.createUserUUID)
            temp
        }
        return Page(list, page, size, totalPage, totalSize)
    }
}