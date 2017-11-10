package com.shun.entity

import java.util.*

/**
d by rainbow on 2017/9/19.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
open class Task {

    var uuid: String? = null

    // 商户名称
    var merchantName: String? = null

    // 记录分类，stick：维护工单、install：新装工单
    var kind: String? = null

    // 类型
    var type: String? = null

    // 维护问题描述及备注
    var remark: String? = null

    //签约人电话
    var signUserMobile: String? = null

    // 设备领取人电话
    var drawUserMobile: String? = null

    //装机人电话
    var installUserMobile: String? = null

    // 装机时间
    var installTime: String? = null

    // 维护人电话
    var serverMobile: String? = null

    // 维护时间
    var serverTime: String? = null

    //0未派发 1：已派发 2：进行中 3：已完成 -1：未完成
    var status: Int? = null

    // 发布时间
    var issueTime: String? = null

    // 创建日期
    var createTime: Date? = null

    // 押金
    var money: Double? = null

    //押金类型（现金、对公账户）
    var moneyType: String? = null

    //通讯费
    var messageMoney: Double? = null

    //通讯费类型（现金、对公账户）
    var messageMoneyType: String? = null

    // 相关图片
    var images: List<String>? = null

    // 维护项目
    var items: List<String>? = null
}