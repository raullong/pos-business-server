package com.shun.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by alwaysbe on 2017/10/26.
 *
 * @Email: lwn1207jak@163.com
 */
@Document(collection = "task")
class TaskEntity : Task() {

    @Id
    var id: String? = null

    // 商户uuid
    var merchantUUID: String? = null

    // 签约人用户uuid
    var signUserUUID: String? = null

    // 设备领取人用户uuid
    var drawUserUUID: String? = null

    // 装机人用户uuid
    var installUserUUID: String? = null

    // 维护人员uuid
    var serverUserUUID: String? = null

    // 创建用户uuid
    var createUserUUID: String? = null

    // 逻辑删除，1：已删除、 0：未删除
    var logicDel: Int? = null
}