package com.shun.controller.api

import com.shun.entity.Task
import com.shun.entity.User
import com.shun.service.TaskService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by rainbow on 2017/9/19.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@RestController
@RequestMapping("/api/v1/task/{kind}")
class TaskController {
    @Autowired
    private lateinit var taskServerService: TaskService


    @PostMapping("/create")
    fun create(@SessionAttribute("user") user: User, @PathVariable(name = "kind") kind: String, @RequestBody taskServer: Task) = taskServerService.create(user, kind, taskServer)

    @GetMapping("/list")
    fun list(@PathVariable(name = "kind") kind: String, @RequestParam params: Map<String, String>) = taskServerService.list(kind, params)

    @GetMapping("/info/{uuid}")
    fun info(@PathVariable(name = "kind") kind: String, @PathVariable(name = "uuid") uuid: String) = taskServerService.info(uuid)

    @PostMapping("/save")
    fun save(@PathVariable(name = "kind") kind: String, @RequestBody taskServer: Task) = taskServerService.save(taskServer)

    @PostMapping("/delete/{uuid}")
    fun del(@PathVariable(name = "kind") kind: String, @PathVariable(name = "uuid") uuid: String) = taskServerService.delete(uuid)
}