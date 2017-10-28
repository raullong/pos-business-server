package com.shun.controller.api

import com.shun.commons.NeedAuth
import com.shun.entity.User
import com.shun.service.UserService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by Administrator on 2017/8/12.
 */
@RestController
@RequestMapping("/api/v1/user")
@Api(tags = arrayOf("用户管理"))
class UserController {


    @Autowired
    private lateinit var userService: UserService

    @NeedAuth
    @GetMapping("/list")
    @ApiOperation("用户列表")
    fun list(@RequestParam params: Map<String, String>) = userService.list(params)

    @NeedAuth
    @PostMapping("/create")
    @ApiOperation("新建用户")
    fun create(@RequestBody user: User) = userService.create(user)

    @NeedAuth
    @GetMapping("/types")
    fun types() = userService.types()

    @NeedAuth
    @PostMapping("/resetPassword/{uuid}")
    fun resetPassword(@PathVariable(name = "uuid") uuid: String) = userService.resetPassword(uuid)

    @GetMapping("/info/{uuid}")
    fun info(@PathVariable uuid: String) = userService.info(uuid)

    @PutMapping("/save")
    fun save(@RequestBody user: User) = userService.save(user)

    @PostMapping("/modify/{mobile}")
    fun modify(@PathVariable mobile: String, @RequestBody params: Map<String, Any>) = userService.modify(mobile, params)

    @GetMapping("/remote/{name}")
    fun remote(@PathVariable(name = "name") name: String) = userService.remote(name)


}