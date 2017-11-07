package com.shun.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by alwaysbe on 2017/10/26.
 *
 * @Email: lwn1207jak@163.com
 */
@Document(collection = "taskServer")
class TaskServerEntity : TaskServer() {

    @Id
    var id: String? = null

    // 商户uuid
    var merchantUUID: String? = null

    //终端编码
    var machineCode: String? = null

    // 维护人员uuid
    var serverUserUUID: String? = null

    // 创建用户uuid
    var createUserUUID: String? = null
}