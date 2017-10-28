package com.shun.service

import com.shun.commons.ApiUtils
import com.shun.entity.Note
import com.shun.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
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


    fun save(request: Note, user: User) {
        requireNotNull(request.title, { "标题不允许为空" })
        requireNotNull(request.content, { "内容不允许为空" })
        requireNotNull(user.uuid, { "用户错误" })

        val note = Note()

        note.content = request.content
        note.title = request.title
        note.createTime = Date()
        note.user = user
        note.type = if (request.type != null && request.type != "") {
            request.type
        } else "通知"
        note.uuid = UUID.randomUUID().toString()
        note.flag = request.flag
        note.searchKey = build(note)

        mongoTemplate.insert(note)

    }

    fun list(params: Map<String, Any?>): Any {
        val criteria = Criteria()

        if (params["searchKey"].toString().isNotBlank()) criteria.and("searchKey").regex(params["searchKey"].toString())

        val query = Query.query(criteria)

        val page = if (params["page"] != null) params["page"].toString().toInt() else 0
        val size = if (params["size"] != null) params["size"].toString().toInt() else 10


        if (page > 0) {
            query.skip((page - 1) * size).limit(size)
        }

        val list = mongoTemplate.find(query, Note::class.java)

        return if (page > 0) {
            mapOf(
                    "list" to list,
                    "page" to page,
                    "size" to size,
                    "total" to mongoTemplate.count(query, Note::class.java)
            )
        } else {
            list
        }
    }


    fun build(t: Note): String? {
        val keys = getSearchKeys(t)
        t.searchKey = if (keys.isNotEmpty()) {
            val first = utils.stringToPinyin(keys[0]!!, "first")
            val full = utils.stringToPinyin(keys[0]!!, "full")
            arrayOf(first.first(), first, full, *keys).distinct().joinToString(",")
        } else {
            ""
        }
        return t.searchKey
    }

    fun getSearchKeys(t: Note) = arrayOf(t.title!!, t.content)
}