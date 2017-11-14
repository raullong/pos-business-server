package com.shun.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by alwaysbe on 2017/10/28.
 *
 * @Email: lwn1207jak@163.com
 */
@Document(collection = "user")
class UserEntity : User() {

    @Id
    var id: String? = null

    // 用户token
    var token: String? = null

    // 逻辑删除，1：已删除、 0：未删除
    var logicDel: Int? = null
}