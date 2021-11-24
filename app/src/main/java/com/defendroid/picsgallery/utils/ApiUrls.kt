package com.defendroid.picsgallery.utils

import com.defendroid.picsgallery.BuildConfig.BASE_URL

object ApiUrls {

    private const val VERSION = "v2"

    const val PHOTO_LIST_URL = "${BASE_URL}$VERSION/list"

    fun getPagedListUrl(pageNumber: String?, limit: String?): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append(PHOTO_LIST_URL)
        stringBuilder.append("?")

        if (!pageNumber.isNullOrBlank()) {
            stringBuilder.append("page=$pageNumber")
            stringBuilder.append("&")
        }

        if (!limit.isNullOrBlank())
            stringBuilder.append("limit=$limit")

        return stringBuilder.toString()
    }
}