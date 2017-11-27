package com.shun.entity

/**
 * Created by alwaysbe on 2017/10/28.
 *
 * @Email: lwn1207jak@163.com
 */
class UserResponse : User() {

    // 用户类型
    var userType: String? = null

    // 位置坐标信息
    var position: Position? = null

    var address: String? = null
}