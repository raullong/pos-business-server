package com.shun.controller.app

import com.shun.commons.NeedAuth
import com.shun.commons.exception.AppException
import com.shun.entity.Gps
import com.shun.entity.User
import com.shun.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by alwaysbe on 2017/11/4.
 *
 * @Email: lwn1207jak@163.com
 */
@RestController
@RequestMapping("/client/user")
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

    @PostMapping("/modifyPassword")
    fun modifyPassword(
            @RequestBody requestParams: Map<String, String>
    ): Any {
        val mobile = requestParams["mobile"]
        val code = requestParams["code"]
        val password = requestParams["password"]
        if (mobile.isNullOrEmpty()) throw AppException("手机号不能为空")
        if (code.isNullOrEmpty()) throw AppException("验证码不能为空")
        if (password.isNullOrEmpty()) throw AppException("密码不能为空")
        return mapOf(
                "code" to "success",
                "message" to "修改密码成功",
                "data" to userService.modifyPassword(mobile!!, code!!, password!!)
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


    @NeedAuth
    @PostMapping("/gps/collect")
    fun aCollectGps(
            @RequestAttribute(name = "user") user: User,
            @RequestBody gps: Gps
    ): Any {
        return mapOf(
                "code" to "success",
                "message" to "用户位置上传成功",
                "data" to userService.aCollectGps(user, gps)
        )
    }


    @NeedAuth
    @GetMapping("/gps/list")
    fun aGpsList(
            @RequestAttribute(name = "user") user: User,
            @RequestParam requestParams: Map<String, String>
    ): Any {
        return mapOf(
                "code" to "success",
                "message" to "获取用户位置信息列表成功",
                "data" to userService.aGpsList(user, requestParams)
        )
    }


    @NeedAuth
    @GetMapping("/near")
    fun nearUser(
            @RequestAttribute(name = "user") user: User
    ): Any {
        return mapOf(
                "code" to "success",
                "message" to "获取附近用户列表成功",
                "data" to userService.queryDistance(user)
        )
    }


    @PostMapping("/info/{uuid}")
    fun aUserInfo(
            @PathVariable(name = "uuid") uuid: String
    ): Any {
        return mapOf(
                "code" to "success",
                "message" to "获取用户详情成功",
                "data" to userService.aUserInfo(uuid)
        )
    }
}