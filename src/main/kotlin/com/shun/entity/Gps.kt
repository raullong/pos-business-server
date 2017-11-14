package com.shun.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by rainbow on 2017/4/26.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Document(collection = "gps")
class Gps : Location() {

    @Id
    val id: String? = null

    // 用户uuid
    var userUUID: String? = null

    // 名称
    var name: String? = null

    // 定位类型：GPS、WIFI、4g
    var locType: String? = null

    // 精度
    var accuracy: Double? = null

    // 创建时间
    var createTime: String? = null

    // 状态
    var status: Int? = null

}
