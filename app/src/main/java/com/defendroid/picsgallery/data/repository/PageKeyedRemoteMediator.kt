package com.defendroid.picsgallery.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bumptech.glide.load.HttpException
import com.defendroid.picsgallery.data.api.ApiHelper
import com.defendroid.picsgallery.data.db.PhotoDb
import com.defendroid.picsgallery.data.db.PhotoRemoteKeyDao
import com.defendroid.picsgallery.data.db.PhotosDao
import com.defendroid.picsgallery.data.model.Photo
import com.defendroid.picsgallery.data.model.PhotoRemoteKey
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PageKeyedRemoteMediator(
    private val db: PhotoDb,
    private val apiHelper: ApiHelper,
) : RemoteMediator<Int, Photo>() {
    private val photoDao: PhotosDao = db.getPhotoDao()
    private val remoteKeyDao: PhotoRemoteKeyDao = db.remoteKeys()

    override suspend fun initialize(): InitializeAction {
        // Require that remote REFRESH is launched on initial load and succeeds before launching
        // remote PREPEND / APPEND.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Photo>
    ): MediatorResult {
        try {
            // Get the closest item from PagingState that we want to load data around.
            val loadKey = when (loadType) {
                REFRESH -> null
                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                APPEND -> {
                    // Query DB for SubredditRemoteKey for the subreddit.
                    // SubredditRemoteKey is a wrapper object we use to keep track of page keys we
                    // receive from the Reddit API to fetch the next or previous page.

                    val remoteKey = db.withTransaction {
                        remoteKeyDao.getRemoteKeys()
                    }

                    // We must explicitly check if the page key is null when appending, since the
                    // Reddit API informs the end of the list by returning null for page key, but
                    // passing a null key to Reddit API will fetch the initial page.
                    if (remoteKey.nextPage == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    remoteKey.nextPage
                }
            }

            val data = apiHelper.getPhotoPaging(
                limit = when (loadType) {
                    REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }.toString(),
                pageNumber = loadKey
            )

            var items : List<Photo>? = null

            data.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    items = it
                }, {
                    it.printStackTrace()
                })

            db.withTransaction {
                if (loadType == REFRESH) {
                    photoDao.deleteAllPhotos()
                    remoteKeyDao.deleteKeys()
                }

                val nextPage = loadKey?.toInt() ?: 0
                remoteKeyDao.insert(PhotoRemoteKey(nextPage = nextPage.toString()))

                items?.let { list->
                    photoDao.insertAll(list)
                }
            }

            return MediatorResult.Success(endOfPaginationReached = items?.isEmpty() == true)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}
