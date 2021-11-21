package com.defendroid.picsgallery.data.api

import com.defendroid.picsgallery.data.model.Photo
import com.defendroid.picsgallery.utils.ApiUrls
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Single

class ApiServiceImpl : ApiService {

    override fun getPhotoList(): Single<List<Photo>> {
        return Rx2AndroidNetworking.get(ApiUrls.PHOTO_LIST_URL)
            .build()
            .getObjectListSingle(Photo::class.java)
    }
}