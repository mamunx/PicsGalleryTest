package com.defendroid.picsgallery.data.api

import com.defendroid.picsgallery.data.model.Photo
import io.reactivex.Single

interface ApiService {
    fun getPhotos(): Single<List<Photo>>
    fun getPhotosPaging(limit: String?, pageNumber: String?): Single<List<Photo>>
}