package com.shun.entity

import com.shun.commons.IDEntity
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

/**
 * Created by alwaysbe on 2017/11/6.
 *
 * @Email: lwn1207jak@163.com
 */
@Document(collection = "note_read")
class NoteReaded : IDEntity() {

    var noteUUID: List<String>? = null

    var userUUID: String? = null

    var status: Int? = null

    var createTime: Date? = null
}