package com.shun.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by alwaysbe on 2017/10/28.
 *
 * @Email: lwn1207jak@163.com
 */
@Document(collection = "user_type")
class UserTypeEntity {

    @Id
    var id: String? = null

    var key: Int? = null

    var value: String? = null

    var status: Int? = null
}