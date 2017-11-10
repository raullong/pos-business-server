package com.shun.config

import com.shun.interceptors.ApiInterceptor
import com.shun.interceptors.AuthInterceptors
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * Created by rainbow on 2017/6/28.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Configuration
class WebConfig : WebMvcConfigurerAdapter() {

    @Value("\${image.path}")
    lateinit private var imagePath: String

    @Autowired
    private lateinit var apiInterceptor: ApiInterceptor

    @Autowired
    lateinit private var authInterceptor: AuthInterceptors

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authInterceptor)
                .excludePathPatterns("/auth/**", "/client/**")
                .addPathPatterns("/app/**")
        registry.addInterceptor(apiInterceptor)
                .addPathPatterns("/api/v1/**", "/client/**")
    }


    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/image/**").addResourceLocations("file:$imagePath")
        super.addResourceHandlers(registry)
    }
}