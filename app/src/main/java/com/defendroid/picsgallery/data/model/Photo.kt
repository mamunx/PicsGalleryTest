package com.defendroid.picsgallery.data.model

import android.os.Parcelable
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.defendroid.picsgallery.utils.AppConstants.THUMB_HEIGHT
import com.defendroid.picsgallery.utils.AppConstants.THUMB_WIDTH
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "photos")
@Parcelize
data class Photo(
    @PrimaryKey
    @SerializedName("id") val id: Long?,
    @SerializedName("author") val author: String?,
    @SerializedName("width") val width: Int?,
    @SerializedName("height") val height: Int?,
    @SerializedName("url") val url: String?,
    @SerializedName("download_url") val download_url: String?
) : Parcelable {
    fun getThumbnailUrl(): String {
        //example url : https://picsum.photos/id/1021/2048/1206
        return download_url?.let {
            try {
                val urlArray = it.split("id/")
                val baseUrl = urlArray[0]
                val url = "${baseUrl}id/$id/$THUMB_WIDTH/$THUMB_HEIGHT"
                Log.d("ThumbURL -> ", url)
                url
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        } ?: ""
    }
}