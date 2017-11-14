package com.shun.controller.app

import com.shun.commons.NeedAuth
import com.shun.entity.Location
import com.shun.entity.User
import com.shun.service.SignService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by alwaysbe on 2017/11/13.
 *
 * @Email: lwn1207jak@163.com
 */
@RestController
@RequestMapping("/client/sign")
class ASignController {

    @Autowired
    private lateinit var signService: SignService

    @NeedAuth
    @PostMapping()
    fun aSign(
            @RequestAttribute(name = "user") user: User,
            @RequestBody position: Location
    ): Any {
        return mapOf(
                "code" to "success",
                "message" to "签到成功",
                "data" to signService.aSign(user, position)
        )
    }
}