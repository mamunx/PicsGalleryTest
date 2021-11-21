package com.defendroid.picsgallery.data.api

class ApiHelper(private val apiService: ApiService) {

    fun getPhotoList() = apiService.getPhotoList()
}