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
    fun create(@RequestBody params: Note, @SessionAttribute("user") user: User) = noteService.create(params, user)

    @GetMapping("/list")
    fun list(@RequestParam params: Map<String, String?>) = noteService.list(params)

    @GetMapping("/info/{uuid}")
    fun info(@PathVariable(name = "uuid") uuid: String) = noteService.info(uuid)

    @PostMapping("/save")
    fun save(@RequestBody note: Note) = noteService.save(note)

    @PostMapping("/delete/{uuid}")
    fun delete(@PathVariable(name = "uuid") uuid: String) = noteService.delete(uuid)

}