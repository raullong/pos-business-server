package com.shun.controller.api

import com.shun.service.SignService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by rainbow on 2017/9/19.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@RestController
@RequestMapping("/api/v1/sign")
class SignController {

    @Autowired
    private lateinit var signService: SignService

    @PostMapping("/create")
    fun create(@RequestBody params: Map<String, Any>) = signService.create(params)

    @GetMapping("/list")
    fun list() = signService.list()
}