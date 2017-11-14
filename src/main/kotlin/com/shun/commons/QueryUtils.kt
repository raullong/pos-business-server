package com.shun.commons

import com.shun.entity.Page
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

/**
 * Created by alwaysbe on 2017/11/13.
 *
 * @Email: lwn1207jak@163.com
 */
@Component
class QueryUtils {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    fun buildQueryInclude(criteria: Criteria, includeFields: List<String>?): Query {
        return buildQuery(criteria, includeFields, null)
    }

    fun buildQueryExclude(criteria: Criteria, excludeFields: List<String>?): Query {
        return buildQuery(criteria, null, excludeFields)
    }

    fun buildQuery(criteria: Criteria): Query {
        return buildQuery(criteria, null, null)
    }

    fun buildQuery(criteria: Criteria, includeFields: List<String>?, excludeFields: List<String>?): Query {
        return buildQuery(criteria, includeFields, excludeFields, null, null)
    }

    fun buildQuery(
            criteria: Criteria,
            includeFields: List<String>?,
            excludeFields: List<String>?,
            descSortKeys: List<String>?,
            ascSortKeys: List<String>?
    ): Query {
        val query = Query.query(criteria)
        if (includeFields != null && includeFields.isNotEmpty()) {
            includeFields.forEach {
                query.fields().include(it)
            }
        }
        if (excludeFields != null && excludeFields.isNotEmpty()) {
            excludeFields.forEach {
                query.fields().exclude(it)
            }
        }

        var flag = 0

        if (descSortKeys != null && descSortKeys.isNotEmpty()) {
            flag += 1
        }
        if (ascSortKeys != null && ascSortKeys.isNotEmpty()) {
            flag += 2
        }

        when (flag) {
            1 -> query.with(Sort(Sort.Direction.DESC, descSortKeys))
            2 -> query.with(Sort(Sort.Direction.ASC, ascSortKeys))
            3 -> query.with(Sort(Sort.Direction.DESC, descSortKeys).and(Sort(Sort.Direction.ASC, ascSortKeys)))
        }
        return query
    }


    fun <T> queryObject(
            criteria: Criteria,
            includeFields: List<String>?,
            excludeFields: List<String>?,
            descSortKeys: List<String>?,
            ascSortKeys: List<String>?,
            page: Int?,
            size: Int?,
            target: Class<T>
    ): Page<T> {
        val query = buildQuery(criteria, includeFields, excludeFields, descSortKeys, ascSortKeys)

        return if (page != null && size != null) {
            val totalSize = mongoTemplate.count(query, target)
            val totalPage = Math.ceil((totalSize / size.toDouble())).toInt()
            val resp = mongoTemplate.find(query, target)

            Page(resp, page, size, totalPage, totalSize)
        } else {
            Page(mongoTemplate.find(query, target), 0, 0, 0, 0)
        }
    }
}