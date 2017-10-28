package com.shun

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.netflix.feign.EnableFeignClients

@SpringBootApplication
@ServletComponentScan
@EnableFeignClients
class ManageServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(ManageServerApplication::class.java, *args)

}
