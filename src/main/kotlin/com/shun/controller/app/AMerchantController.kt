package com.shun.controller.app

import com.shun.entity.Location
import com.shun.service.MerchantService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by alwaysbe on 2017/11/14.
 *
 * @Email: lwn1207jak@163.com
 */
@RestController
@RequestMapping("/client/merchant")
class AMerchantController {

    @Autowired
    private lateinit var service: MerchantService


    @PostMapping("/collect/{uuid}")
    fun collectLocation(
            @PathVariable(name = "uuid") uuid: String,
            @RequestBody location: Location
    ): Any {
        return mapOf(
                "code" to "success",
                "message" to "商户位置采集成功",
                "data" to service.aCollectLocation(uuid, location)
        )
    }

}