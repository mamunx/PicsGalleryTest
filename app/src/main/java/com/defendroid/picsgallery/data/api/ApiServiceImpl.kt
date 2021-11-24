package com.defendroid.picsgallery.data.api

import com.defendroid.picsgallery.data.model.Photo
import com.defendroid.picsgallery.utils.ApiUrls
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Single

class ApiServiceImpl : ApiService {

    override fun getPhotos(): Single<List<Photo>> {
        return Rx2AndroidNetworking.get(ApiUrls.PHOTO_LIST_URL)
            .build()
            .getObjectListSingle(Photo::class.java)
    }

    override fun getPhotosPaging(
        limit: String?,
        pageNumber: String?
    ): Single<List<Photo>> {
        return Rx2AndroidNetworking.get(ApiUrls.getPagedListUrl(pageNumber, limit))
            .build()
            .getObjectListSingle(Photo::class.java)
    }
}