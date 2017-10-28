package com.shun.entity

import com.shun.commons.IDEntity
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

/**
 * Created by rainbow on 2017/9/19.
 *一事专注，便是动人；一生坚守，便是深邃！
 */
@Document(collection = "sign")
class Sign : IDEntity() {

    var user: User? = null

    var time: Date? = null

    var point: Point? = null

    var status: Int? = null

}