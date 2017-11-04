package com.shun.entity

/**
 * Created by rainbow on 2017/9/19.
 *一事专注，便是动人；一生坚守，便是深邃！
 *
 * 用户签到信息
 */
open class Sign {

    // 签到日期
    var date: String? = null

    // 签到时间
    var time: String? = null

    // 签到位置信息
    var position: Location? = null

    var status: Int? = null
}