package com.defendroid.picsgallery.data.repository

import com.defendroid.picsgallery.data.api.ApiHelper
import com.defendroid.picsgallery.data.model.Photo
import io.reactivex.Single

class PhotoRepository(private val apiHelper: ApiHelper) {

    fun getPhotoList(): Single<List<Photo>> {
        return apiHelper.getPhotoList()
    }
}