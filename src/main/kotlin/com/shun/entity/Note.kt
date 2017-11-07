package com.shun.entity

import java.util.*

/**
 * Created by Administrator on 2017/8/13.
 */
open class Note {

    // 唯一标识
    var uuid: String? = null

    //标题
    var title: String? = null

    //内容
    var content: String? = null

    // 图片
    var images: List<String>? = null

    //创建时间
    var createTime: Date? = null

    //公告类型
    var type: String? = null

    //是否紧急 1:是，0:否
    var urgency: Int? = null

    // 状态
    var status: Int? = null

    // 标记是否删除，1：已删除、0：未删除
    var logicDel: Int? = null
}