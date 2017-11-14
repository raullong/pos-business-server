package com.shun.controller.app

import com.shun.commons.NeedAuth
import com.shun.entity.User
import com.shun.service.NoteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * Created by alwaysbe on 2017/11/4.
 *
 * @Email: lwn1207jak@163.com
 */
@RestController
@RequestMapping("/client/note")
class ANoteController {

    @Autowired
    private lateinit var noteService: NoteService


    @GetMapping("/urgency/list")
    fun urgencyNoteList(
            @RequestParam requestParams: Map<String, String?>
    ): Any {
        return mapOf(
                "code" to "success",
                "message" to "获取紧急通知信息列表成功",
                "data" to noteService.urgencyNoteList(requestParams)
        )
    }

    @NeedAuth
    @GetMapping("/list")
    fun appNoteList(
            @RequestAttribute(name = "user") user: User,
            @RequestParam requestParams: Map<String, String?>
    ): Any {
        return mapOf(
                "code" to "success",
                "message" to "获取通知公告信息列表成功",
                "data" to noteService.appNoteList(user, requestParams)
        )
    }

    @NeedAuth
    @PostMapping("/info/{uuid}")
    fun appNoteInfo(
            @RequestAttribute(name = "user") user: User,
            @PathVariable(name = "uuid") uuid: String
    ): Any {
        return mapOf(
                "code" to "success",
                "message" to "获取通知公告详情成功",
                "data" to noteService.appNoteInfo(user, uuid)
        )
    }

    @NeedAuth
    @PostMapping("/create")
    fun appNoteCreate(
            @RequestAttribute(name = "user") user: User,
            @RequestParam(name = "title") title: String,
            @RequestParam(name = "content") content: String,
            @RequestParam(name = "urgency", required = false, defaultValue = "0") urgency: Int,
            @RequestParam(name = "images", required = false) images: List<MultipartFile>?
    ): Any {
        return mapOf(
                "code" to "success",
                "message" to "通知公告发布成功",
                "data" to noteService.appNoteCreate(user, title, content, urgency, images)
        )
    }
}