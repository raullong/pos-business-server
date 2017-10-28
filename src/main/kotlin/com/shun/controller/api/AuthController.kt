package com.shun.controller.api

import com.shun.commons.USER_TYPE_MANAGE
import com.shun.commons.exception.AppException
import com.shun.entity.User
import com.shun.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

/**
 * Created by rainbow on 2017/8/9.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    @Autowired
    lateinit private var authService: AuthService

    @PostMapping("/login")
    fun login(@RequestBody params: Map<String, String>, session: HttpSession) {
        val mobile = params["mobile"] ?: throw AppException("登录手机号不能为空")
        val password = params["password"] ?: throw AppException("登录密码不能为空")
        session.setAttribute("user", authService.login(mobile, password, USER_TYPE_MANAGE))
    }

    @PutMapping("/logout")
    fun logout(session: HttpSession) {
        session.invalidate()
    }

    @PostMapping("/info")
    fun info(@SessionAttribute(name = "user") user: User) = authService.info(user.mobile!!)
}