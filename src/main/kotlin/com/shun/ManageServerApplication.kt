package com.shun

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ManageServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(ManageServerApplication::class.java, *args)

}
