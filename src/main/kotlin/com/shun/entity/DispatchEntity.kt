package com.shun.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by alwaysbe on 2017/10/27.
 *
 * @Email: lwn1207jak@163.com
 */
@Document(collection = "dispatch")
class DispatchEntity : Dispatch() {

    @Id
    var id: String? = null

    // 商户uuid
    var merchantUUID: String? = null

    // 创建用户uuid
    var createUserUUID: String? = null
}