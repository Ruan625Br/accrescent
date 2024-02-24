package app.accrescent.client.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Media(
    val id: String,
    val url: String
) : Parcelable