package com.shun.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by alwaysbe on 2017/11/4.
 *
 * @Email: lwn1207jak@163.com
 */
@Document(collection = "note")
class NoteEntity : Note() {

    @Id
    var id: String? = null

    //公告发布用户uuid
    var createUserUUID: String? = null
}