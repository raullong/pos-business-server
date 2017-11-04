package com.shun.entity

import java.util.*

/**
 * Created by Administrator on 2017/7/30.
 */
open class User {

    // 用户uuid
    var uuid: String? = null

    // 用户姓名
    var username: String? = null

    // 用户手机号码
    var mobile: String? = null

    // 昵称
    var nickname: String? = null

    // 登陆密码
    var password: String? = null

    // 用户状态
    var status: Int? = null

    //用户类别 1 管理员 2 App客户端用户 3 第三方用户
    // 用户类别
    var type: Int? = null

    // 平台
    var client: String? = null

    // 创建时间
    var createTime: Date? = null

    // 最后一次更新时间
    var lastTime: Date? = null

    // 位置信息
    var position: Location? = null

    // 是否明星员工， 1：是、0：否
    var superStar: Int? = null
}