package app.accrescent.client.data.models

import app.accrescent.client.data.db.App


data class Ads(
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val type: AdsType,
    val clickAction: AdsAction,
    val appId: String? = null,
    val app: App? = null
)
