package com.shun.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by alwaysbe on 2017/11/15.
 *
 * @Email: lwn1207jak@163.com
 */
@Document(collection = "gps")
class GpsEntity : Gps() {

    @Id
    val id: String? = null

    // 用户uuid
    var userUUID: String? = null

    // 创建时间
    var createTime: String? = null

    // 状态
    var status: Int? = null
}