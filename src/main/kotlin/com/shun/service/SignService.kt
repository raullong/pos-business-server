package com.shun.service

import com.shun.entity.Sign
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service

/**
 * Created by rainbow on 2017/9/19.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Service
class SignService {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    fun create(params: Map<String, Any>) {

    }

    fun list() = mongoTemplate.findAll(Sign::class.java)
}