package com.shun.controller.api

import com.shun.entity.TaskServer
import com.shun.entity.User
import com.shun.service.TaskServerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by rainbow on 2017/9/19.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@RestController
@RequestMapping("/api/v1/taskServer")
class TaskServerController {
    @Autowired
    private lateinit var taskServerService: TaskServerService


    @PostMapping("/create")
    fun create(@SessionAttribute("user") user: User, @RequestBody taskServer: TaskServer) = taskServerService.create(user, taskServer)

    @GetMapping("/list")
    fun list(@RequestParam params: Map<String, String>) = taskServerService.list(params)

    @GetMapping("/info/{uuid}")
    fun info(@PathVariable(name = "uuid") uuid: String) = taskServerService.info(uuid)

    @PostMapping("/save")
    fun save(@RequestBody taskServer: TaskServer) = taskServerService.save(taskServer)

    @PostMapping("/delete/{uuid}")
    fun del(@PathVariable(name = "uuid") uuid: String) = taskServerService.delete(uuid)
}