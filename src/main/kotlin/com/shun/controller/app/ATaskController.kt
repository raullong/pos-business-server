package com.shun.controller.app

import com.shun.commons.NeedAuth
import com.shun.entity.User
import com.shun.service.TaskService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * Created by alwaysbe on 2017/11/13.
 *
 * @Email: lwn1207jak@163.com
 */
@RestController
@RequestMapping("/client/task")
class ATaskController {

    @Autowired
    private lateinit var service: TaskService


    @NeedAuth
    @GetMapping("/cur")
    fun curTask(
            @RequestAttribute(name = "user") user: User,
            @RequestParam(name = "page", required = false, defaultValue = "1") page: Int,
            @RequestParam(name = "size", required = false, defaultValue = "10") size: Int
    ): Any {
        return mapOf(
                "code" to "success",
                "message" to "获取今日任务列表成功",
                "data" to service.curTask(user, page, size)
        )
    }

    @PostMapping("/info/{uuid}")
    fun aTaskInfo(
            @PathVariable(name = "uuid") uuid: String
    ): Any {
        return mapOf(
                "code" to "success",
                "message" to "获取任务详情成功",
                "data" to service.aTaskInfo(uuid)
        )
    }

    @NeedAuth
    @PostMapping("/do")
    fun aDoTask(
            @RequestAttribute(name = "user") user: User,
            @RequestParam(name = "uuid") uuid: String,
            @RequestParam(name = "items", required = false) items: String?,
            @RequestParam(name = "messageMoney", required = false) messageMoney: Double?,
            @RequestParam(name = "messageMoneyType", required = false) messageMoneyType: String?,
            @RequestParam(name = "money", required = false) money: Double?,
            @RequestParam(name = "moneyType", required = false) moneyType: String?,
            @RequestParam(name = "remark", required = false) remark: String?,
            @RequestParam(name = "images", required = false) images: List<MultipartFile>?
    ): Any {
        return mapOf(
                "code" to "success",
                "message" to "任务结果提交成功",
                "data" to service.doTask(user, uuid, items, messageMoney, messageMoneyType, money, moneyType, remark, images)
        )
    }
}