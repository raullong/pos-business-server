package com.shun.entity

import com.shun.commons.IDEntity
import java.util.*

/**
 * Created by alwaysbe on 2017/10/26.
 *
 * @Email: lwn1207jak@163.com
 *
 * 位置信息
 */
class Location : IDEntity() {

    // 唯一标识
    var uuid: String? = null

    // 纬度
    var lat: Double? = null

    // 经度
    var lng: Double? = null

    // 定位类型：GPS、WIFI、4g
    var locType: String? = null

    // 精度
    var accuracy: String? = null

    // 地址
    var address: String? = null

    // 创建时间
    var createTime: Date? = null

    // 状态
    var status: Int? = null
}