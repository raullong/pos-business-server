package com.shun.feign

import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Created by Administrator on 2017/8/12.
 */
@FeignClient("app", url = "\${rainbow.app.user.url}", configuration = arrayOf(FeignConfig::class))
interface AppClient {

    //App用户登录
    @RequestMapping("/api/v1/user/login", method = arrayOf(RequestMethod.POST))
    fun login(@RequestBody map: Map<String, String>): Map<String, Any?>
}