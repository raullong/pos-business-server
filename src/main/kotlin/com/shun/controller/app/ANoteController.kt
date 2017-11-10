package com.shun.controller.app

import com.shun.service.NoteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
}