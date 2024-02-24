package app.accrescent.client.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaListInfo(
    val mediaList: List<Media>,
    val currentMediaId: String
) : Parcelable
