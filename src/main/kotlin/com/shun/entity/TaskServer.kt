package com.shun.entity

import java.util.*

/**
d by rainbow on 2017/9/19.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
open class TaskServer {

    var uuid: String? = null

    // 商户名称
    var merchantName: String? = null

    // 商户编码
    var merchantCode: String? = null

    // 维护问题描述
    var question: String? = null

    // 维护人电话
    var serverMobile: String? = null

    //0未派发 1：已派发 2：进行中 3：已完成 -1：未完成
    var status: Int? = null

    // 维护时间
    var taskTime: String? = null

    // 发布时间
    var issueTime: String? = null

    // 创建日期
    var createTime: Date? = null

    //维护类型
    var type: String? = null

    // 逻辑删除，1：已删除、 0：未删除
    var logicDel: Int? = null
}