package com.shun.entity

import com.shun.commons.IDEntity
import java.util.*

/**
 * Created by alwaysbe on 2017/10/26.
 *
 * @Email: lwn1207jak@163.com
 *
 * 商户实体
 */
open class Merchant {

    // 商户唯一标识
    var uuid: String? = null

    // 商户名称
    var name: String? = null

    // 商户编码
    var code: String? = null

    // 商户地址
    var address: String? = null

    // 商户坐标信息
    var locationInfo: Location? = null

    // 商户状态
    var status: Int? = null

    // 备注
    var remark: String? = null

    // 图片
    var images: List<String>? = null

    // 商户添加时间
    var createTime: Date? = null

    // 商户联系人电话
    var linkerMobile: String? = null

    // 商户联系人姓名
    var linkerName: String? = null

    //终端编码
    var machineCode: String? = null
}