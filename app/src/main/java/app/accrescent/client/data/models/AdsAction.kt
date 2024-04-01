package app.accrescent.client.data.models


sealed class AdsAction {
    data object ShowFullScreenAds : AdsAction()
    data class ShowAppsFromAdCategory(val appCategory: AppCategory) : AdsAction()
    data class OpenLink(val link: String) : AdsAction()
    data object ShowAppDetails : AdsAction()
    data object None : AdsAction()
}