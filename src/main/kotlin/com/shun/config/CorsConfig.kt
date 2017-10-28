package com.shun.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

/**
 * Created by rainbow on 2017/8/29.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Configuration
class CorsConfig {

    private fun buildConfig(): CorsConfiguration {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("*")
        config.allowedHeaders = listOf("*")
        config.allowedMethods = listOf("*")
        return config
    }

    @Bean
    open fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", buildConfig())
        return CorsFilter(source)
    }
}