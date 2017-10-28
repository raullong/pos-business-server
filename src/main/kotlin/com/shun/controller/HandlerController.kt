package com.shun.controller

import com.shun.commons.exception.AppException
import com.shun.commons.exception.TokenException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * Created by rainbow on 2017/6/15.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@ControllerAdvice
class HandlerController {

    private val logger by lazy { LoggerFactory.getLogger(HandlerController::class.java) }

    @ExceptionHandler
    fun handler(ex: Exception): ResponseEntity<Any> {
        val parseError = AppException.parse(ex)
        val body = mapOf(
                "code" to "failure",
                "message" to parseError.message,
                "data" to ""
        )

        val status = when (parseError) {
            is TokenException -> HttpStatus.UNAUTHORIZED
            else -> HttpStatus.OK
        }

        logger.info(parseError.message)
        return ResponseEntity(body, status)
    }
}