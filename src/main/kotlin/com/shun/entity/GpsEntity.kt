package com.shun.entity

import com.shun.commons.IDEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import javax.annotation.Generated

/**
 * Created by rainbow on 2017/4/26.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Document(collection = "gps")
class GpsEntity {
    @Id
    @Generated
    var id: String? = null

    //用户id
    var userID: String? = null

    // 用户姓名
    var alias: String? = null

    //设备ID
    var deviceID: String? = null

    //经度
    var latitude: Double? = null

    //纬度
    var longitude: Double? = null

    //经纬度米级经度
    var accuracy: Double? = null

    // 角度
    var angle: Double? = null

    // 定位时间戳
    var timestamp: Date? = null

    // 定位省份
    var province: String? = null

    // 定位城市
    var city: String? = null

    // 定位地址
    var address: String? = null

    // 兴趣点
    var poiName: String? = null

}
