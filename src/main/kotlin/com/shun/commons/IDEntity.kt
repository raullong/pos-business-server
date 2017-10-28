package com.shun.commons

import org.springframework.data.annotation.Id

/**
 * Created by Administrator on 2017/8/13.
 */
open class IDEntity {

    @Id
    var id: String? = null
}