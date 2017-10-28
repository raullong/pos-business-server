package com.shun.controller.api

import com.shun.entity.Dispatch
import com.shun.entity.User
import com.shun.service.DispatchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by Administrator on 2017/8/13.
 */
@RestController
@RequestMapping("/api/v1/dispatch")
class DispatchController {

    @Autowired
    private lateinit var dispatchService: DispatchService

    @PostMapping("/create")
    fun create(@SessionAttribute("user") user: User, @RequestBody dispatch: Dispatch) = dispatchService.create(user, dispatch)

    @GetMapping("/list")
    fun list(@RequestParam params: Map<String, String?>) = dispatchService.list(params)

    @GetMapping("/info/{uuid}")
    fun info(@PathVariable(name = "uuid") uuid: String) = dispatchService.info(uuid)

    @PostMapping("/save")
    fun save(@RequestBody dispatch: Dispatch) = dispatchService.save(dispatch)

    @PostMapping("/delete/{uuid}")
    fun delete(@PathVariable(name = "uuid") uuid: String) = dispatchService.delete(uuid)
}