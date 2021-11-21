package com.defendroid.picsgallery.data.api

import com.defendroid.picsgallery.data.model.Photo
import io.reactivex.Single

interface ApiService {
    fun getPhotoList(): Single<List<Photo>>
}