package com.shun.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

/**
 * Created by alwaysbe on 2017/11/20.
 *
 * @Email: lwn1207jak@163.com
 */
@Document(collection = "user_position")
class UserPosition {

    @Id
    val id: String? = null

    // 用户uuid
    var userUUID: String? = null

    // 位置坐标信息
    var position: Position? = null

    // 地址
    var address: String? = null

    // 创建时间
    var createTime: Date? = null
}