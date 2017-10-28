package com.shun.entity

import com.shun.commons.IDEntity
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

/**
 * Created by Administrator on 2017/8/13.
 */
@Document(collection = "note")
class Note : IDEntity() {

    //标题
    var title: String? = null

    //内容
    var content: String? = null

    //公告发布人
    var user: User? = null

    //创建时间
    var createTime: Date? = null

    //公告类型
    var type: String? = null

    var uuid: String? = null

    var searchKey: String? = null

    //是否紧急 1:是，0:否
    var flag: Int? = null


}