package com.shun.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by alwaysbe on 2017/11/4.
 *
 * @Email: lwn1207jak@163.com
 */
@Document(collection = "sign")
class SignEntity : Sign() {

    @Id
    var id: String? = null

    // 签到用户uuid
    var createUserUUID: String? = null
}