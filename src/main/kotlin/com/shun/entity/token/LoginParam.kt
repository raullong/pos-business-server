package com.shun.entity.token

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "login")
class LoginParam {

    var clientId: String? = null

    var username: String? = null

    var password: String? = null

    var captchaCode: String? = null

    var captchaValue: String? = null

}