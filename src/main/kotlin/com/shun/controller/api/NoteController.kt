package com.shun.controller.api

import com.shun.entity.Note
import com.shun.entity.User
import com.shun.service.NoteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by Administrator on 2017/8/13.
 */
@RestController
@RequestMapping("/api/v1/note")
class NoteController {

    @Autowired
    private lateinit var noteService: NoteService


    @PostMapping("/create")
    fun save(@RequestBody params: Note, @SessionAttribute("user") user: User) = noteService.save(params, user)

    @GetMapping("/list")
    fun list(@RequestParam params: Map<String, Any?>) = noteService.list(params)

}