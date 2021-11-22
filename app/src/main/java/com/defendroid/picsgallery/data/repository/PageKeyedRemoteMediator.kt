package com.defendroid.picsgallery.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.defendroid.picsgallery.data.api.ApiHelper
import com.defendroid.picsgallery.data.db.PhotoDb
import com.defendroid.picsgallery.data.db.PhotoRemoteKeyDao
import com.defendroid.picsgallery.data.db.PhotosDao
import com.defendroid.picsgallery.data.model.Photo
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
                    if (remoteKey.nextPageKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    remoteKey.nextPageKey
                }
            }

            val data = apiHelper.getPhotoList(
                subreddit = subredditName,
                after = loadKey,
                before = null,
                limit = when (loadType) {
                    REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }
            ).data

            val items = data.children.map { it.data }

            db.withTransaction {
                if (loadType == REFRESH) {
                    photoDao.deleteBySubreddit(subredditName)
                    remoteKeyDao.deleteBySubreddit(subredditName)
                }

                remoteKeyDao.insert(SubredditRemoteKey(subredditName, data.after))
                photoDao.insertAll(items)
            }

            return MediatorResult.Success(endOfPaginationReached = items.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}
