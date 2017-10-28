package com.shun.interceptors

import com.shun.commons.NeedAuth
import com.shun.commons.exception.TokenException
import com.shun.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by Administrator on 2017/7/8.
 */
@Component
class ApiInterceptor : HandlerInterceptorAdapter() {

    @Autowired
    lateinit private var userService: UserService

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && handler.hasMethodAnnotation(NeedAuth::class.java)) {
            val user = if (request.getHeader("USER-TOKEN") != null) {
                userService.getByToken(request.getHeader("USER-TOKEN") ?: throw TokenException("USER-TOKEN 不能为空"))
            } else {
                request.session.getAttribute("user") ?: throw TokenException("USER-TOKEN 不能为空")
            }

            request.setAttribute("user", user)
        }
        return true
    }
}