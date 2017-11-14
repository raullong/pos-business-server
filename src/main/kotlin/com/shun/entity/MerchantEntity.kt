package com.shun.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by alwaysbe on 2017/10/26.
 *
 * @Email: lwn1207jak@163.com
 */
@Document(collection = "merchant")
class MerchantEntity : Merchant() {

    @Id
    var id: String? = null

    // 创建用户uuid
    var createUserUUID: String? = null

    // 逻辑删除，1：已删除、 0：未删除
    var logicDel: Int? = null
}