package com.defendroid.picsgallery.data.api

class ApiHelper(private val apiService: ApiService) {

    fun getPhotos() = apiService.getPhotos()

    fun getPhotoPaging(limit: String?, pageNumber: String? = null) =
        apiService.getPhotosPaging(limit, pageNumber)
}