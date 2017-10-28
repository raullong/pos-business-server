package com.shun.entity.token

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "token")
class AccessToken {

    var access_token: String? = null

    var token_type: String? = null

    var expires_in: Long? = null


}