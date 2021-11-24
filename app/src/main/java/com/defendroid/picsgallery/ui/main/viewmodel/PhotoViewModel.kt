package com.defendroid.picsgallery.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.cachedIn
import com.defendroid.picsgallery.data.model.Photo
import com.defendroid.picsgallery.data.repository.PhotoRepository
import com.defendroid.picsgallery.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class PhotoViewModel(private val photoRepository: PhotoRepository) : ViewModel() {

    private val photoListLiveData = MutableLiveData<Resource<List<Photo>>>()

    private val compositeDisposable = CompositeDisposable()


    init {
        fetchPhotos()
    }

    private fun fetchPhotos() {
        photoListLiveData.postValue(Resource.loading(null))
        compositeDisposable.add(
            photoRepository.getPhotos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    photoListLiveData.postValue(Resource.success(it))
                }, {
                    photoListLiveData.postValue(
                        Resource.error(
                            it.localizedMessage ?: "Something Went Wrong", null
                        )
                    )
                })
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts = photoRepository.getPhotosPaged(30)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getPhotoList(): LiveData<Resource<List<Photo>>> {
        return photoListLiveData
    }
}