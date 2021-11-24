package com.defendroid.picsgallery.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.defendroid.picsgallery.data.api.ApiHelper
import com.defendroid.picsgallery.data.db.PhotoDb
import com.defendroid.picsgallery.data.repository.PhotoRepository
import com.defendroid.picsgallery.ui.main.viewmodel.PhotoViewModel

class ViewModelFactory(private val db: PhotoDb, private val apiHelper: ApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoViewModel::class.java)) {
            return PhotoViewModel(PhotoRepository(db, apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}