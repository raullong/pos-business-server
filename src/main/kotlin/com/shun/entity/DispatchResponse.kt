package com.shun.entity

/**
 * Created by alwaysbe on 2017/10/27.
 *
 * @Email: lwn1207jak@163.com
 */
class DispatchResponse : Dispatch() {

    // 商户信息
    var merchant: Merchant? = null

    // 签约人用户
    var signUser: User? = null

    // 设备领取人用户
    var drawUser: User? = null

    // 装机人用户
    var installUser: User? = null

    // 创建用户
    var createUser: User? = null
}