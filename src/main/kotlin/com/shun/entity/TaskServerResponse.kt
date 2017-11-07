package com.shun.entity

/**
 * Created by alwaysbe on 2017/10/26.
 *
 * @Email: lwn1207jak@163.com
 */
class TaskServerResponse : TaskServer() {

    // 商户信息
    var merchant: Merchant? = null

    // 维护人用户
    var serverUser: User? = null

    // 创建用户
    var createUser: User? = null
}