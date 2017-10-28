package com.shun.controller.api

import com.shun.commons.NeedAuth
import com.shun.entity.Merchant
import com.shun.entity.User
import com.shun.service.MerchantService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by alwaysbe on 2017/10/26.
 *
 * @Email: lwn1207jak@163.com
 */
@RestController
@RequestMapping("/api/v1/merchant")
class MerchantController {

    @Autowired
    private lateinit var service: MerchantService

    @NeedAuth
    @GetMapping("/list")
    fun list(@RequestParam params: Map<String, String?>) = service.list(params)

    @NeedAuth
    @PostMapping("/create")
    fun create(@SessionAttribute("user") user: User, @RequestBody merchant: Merchant) = service.create(user, merchant)

    @NeedAuth
    @GetMapping("/info/{uuid}")
    fun info(@PathVariable(name = "uuid") uuid: String) = service.info(uuid)

    @NeedAuth
    @PostMapping("/save")
    fun save(@RequestBody merchant: Merchant) = service.save(merchant)

    @NeedAuth
    @PostMapping("/delete/{uuid}")
    fun delete(@PathVariable(name = "uuid") uuid: String) = service.delete(uuid)

    @NeedAuth
    @GetMapping("/remote/{name}")
    fun remote(@PathVariable(name = "name") name: String) = service.remote(name)

    @NeedAuth
    @GetMapping("/mapList")
    fun mapList() = service.mapList()
}