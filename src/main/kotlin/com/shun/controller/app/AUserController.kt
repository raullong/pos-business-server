package com.shun.controller.app

import com.shun.commons.exception.AppException
import com.shun.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by alwaysbe on 2017/11/4.
 *
 * @Email: lwn1207jak@163.com
 */
@RestController
@RequestMapping("/app/user")
class AUserController {

    @Autowired
    private lateinit var userService: UserService

    @PostMapping("/login")
    fun login(
            @RequestBody requestParams: Map<String, String>
    ): Any {
        val username = requestParams["username"]
        val password = requestParams["password"]
        if (username.isNullOrEmpty()) throw AppException("用户名不能为空")
        if (password.isNullOrEmpty()) throw AppException("密码不能为空")

        return mapOf(
                "code" to "success",
                "message" to "登录成功",
                "data" to userService.appLogin(username!!, password!!)
        )
    }

    @GetMapping("/superStar")
    fun superStar(): Any {
        return mapOf(
                "code" to "success",
                "message" to "获取明星员工信息列表成功",
                "data" to userService.superStar()
        )
    }
}