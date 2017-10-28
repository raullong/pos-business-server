package com.shun.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration

/**
 * Created by alwaysbe on 2017/10/28.
 *
 * @Email: lwn1207jak@163.com
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    override fun configure(http: HttpSecurity) {

        // 开启跨域支持
        http.cors().configurationSource {
            CorsConfiguration().apply {
                allowCredentials = true
                allowedHeaders = listOf("*")
                allowedMethods = listOf("*")
                allowedOrigins = listOf("*")
                maxAge = 3600
            }
        }

        // 关闭 csrf
        http.csrf().disable()

        // 禁用缓存
        http.headers().cacheControl().disable()
    }
}