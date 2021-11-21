package com.defendroid.picsgallery.data.model

import android.os.Parcelable
import com.defendroid.picsgallery.utils.AppConstants.THUMB_HEIGHT
import com.defendroid.picsgallery.utils.AppConstants.THUMB_WIDTH
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
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
                val urlFirstPart = urlArray[0]
                val valueArray = urlArray[1].split("/")
                val id = valueArray[0]

                "$urlFirstPart$id/$THUMB_WIDTH/$THUMB_HEIGHT"
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        } ?: ""
    }
}
