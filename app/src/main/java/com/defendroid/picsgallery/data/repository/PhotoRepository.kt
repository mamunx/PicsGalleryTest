package com.defendroid.picsgallery.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.defendroid.picsgallery.data.api.ApiHelper
import com.defendroid.picsgallery.data.db.PhotoDb
import com.defendroid.picsgallery.data.model.Photo
import io.reactivex.Single

class PhotoRepository(private val db: PhotoDb, private val apiHelper: ApiHelper) :PhotoRepositoryInterface{

    fun getPhotoList(): Single<List<Photo>> {
        return apiHelper.getPhotoList()
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPhotos(subReddit: String, pageSize: Int) = Pager(
        config = PagingConfig(pageSize),
        remoteMediator = PageKeyedRemoteMediator(db, redditApi, subReddit)
    ) {
        db.posts().postsBySubreddit(subReddit)
    }.flow
}