package com.shun.entity

/**
 * Created by rainbow on 2017/4/26.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
open class Gps {

    // 名称
    var name: String? = null

    // 定位类型：GPS、WIFI、4g
    var locType: String? = null

    // 精度
    var accuracy: Double? = null

    // 坐标
    var coordinate: Coordinate? = null

    // 地址
    var address: String? = null

}
