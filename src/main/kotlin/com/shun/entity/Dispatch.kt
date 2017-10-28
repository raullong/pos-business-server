package com.shun.entity

import java.util.*

/**
 * Created by Administrator on 2017/8/13.
 */
open class Dispatch {

    var uuid: String? = null

    // 商户名称
    var merchantName: String? = null

    // 商户编码
    var merchantCode: String? = null

    // 终端编码
    var machineCode: String? = null

    // 类型
    var type: String? = null

    // 押金
    var money: Double? = null

    //押金类型
    var moneyType: String? = null

    //签约人姓名
    var signUserName: String? = null

    //签约人电话
    var signUserMobile: String? = null

    // 设备领取人姓名
    var drawUserName: String? = null

    // 设备领取人电话
    var drawUserMobile: String? = null

    //装机人姓名
    var installUserName: String? = null

    //装机人电话
    var installUserMobile: String? = null

    // 装机时间
    var installTime: String? = null

    // 备注
    var remark: String? = null

    //0未派发 1：已派发 2：进行中 3：已完成 -1：未完成
    var status: Int? = null

    // 创建时间
    var createTime: Date? = null

    //通讯费
    var messageMoney: Double? = null

}