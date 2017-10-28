package com.shun.entity

/**
 * Created by alwaysbe on 2017/10/26.
 *
 * @Email: lwn1207jak@163.com
 */
data class Page<out T>(
        val list: List<T>,
        val curPage: Int,
        val curSize: Int,
        val totalPage: Int,
        val totalSize: Long
)