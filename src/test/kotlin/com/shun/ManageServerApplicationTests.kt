package com.shun

import com.shun.entity.UserEntity
import com.shun.entity.UserPosition
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest
class ManageServerApplicationTests {

    @Autowired
    private lateinit var mongo: MongoTemplate

    @Test
    fun contextLoads() {
        val users = mongo.findAll(UserEntity::class.java)

        users.forEach {
            val p = UserPosition()
            p.userUUID = it.uuid
            p.createTime = Date()

            mongo.insert(p)
        }

    }
}
