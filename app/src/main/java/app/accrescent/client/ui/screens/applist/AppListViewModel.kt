package app.accrescent.client.ui.screens.applist

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.accrescent.client.Accrescent
import app.accrescent.client.R
import app.accrescent.client.data.AppInstallStatuses
import app.accrescent.client.data.RepoDataRepository
import app.accrescent.client.data.db.App
import app.accrescent.client.data.models.Ads
import app.accrescent.client.data.models.AdsAction
import app.accrescent.client.data.models.AdsType
import app.accrescent.client.data.models.AppCategory
import app.accrescent.client.util.getPackageInstallStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import okhttp3.internal.immutableListOf
import java.io.FileNotFoundException
import java.net.ConnectException
import java.net.UnknownHostException
import java.security.GeneralSecurityException
import javax.inject.Inject

@HiltViewModel
class AppListViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repoDataRepository: RepoDataRepository,
    appInstallStatuses: AppInstallStatuses,
) : AndroidViewModel(context as Application) {
    val apps = repoDataRepository.getApps()

    // for saving and restoring the scroll state, mapped to the navigation route
    var firstVisibleItems = mutableMapOf<String?, Pair<Int, Int>>()

    // Initialize install status for apps as they're added
    init {
        val flow = apps.onEach { apps ->
            for (app in apps) {
                val latestVersionCode = try {
                    repoDataRepository.getAppRepoData(app.id).versionCode
                } catch (e: Exception) {
                    null
                }
                setupAds(app)
                appInstallStatuses.statuses[app.id] =
                    context.packageManager.getPackageInstallStatus(app.id, latestVersionCode)
            }
        }
        viewModelScope.launch {
            flow.collect()
        }
    }

    val installStatuses = appInstallStatuses.statuses
    var isRefreshing by mutableStateOf(false)
        private set
    var error: String? by mutableStateOf(null)

    //TODO(Implement real logic)
    var adsList by  mutableStateOf(listOf(Ads(
        title = "Top apps for education",
        subtitle = "Our educational titles",
        imageUrl = "https://i.ibb.co/Sf8w4r3/img0.webp",
        type = AdsType.OFFICIAL,
        clickAction = AdsAction.ShowAppsFromAdCategory(AppCategory.EDUCATIONAL)
    ), Ads(
        title = "Arcticons: Your Customizable Icon Pack for Android",
        subtitle = "Enhance your apps with sleek and customizable vector icons.",
        imageUrl = "https://raw.githubusercontent.com/Arcticons-Team/Arcticons/main/github/header-background.png",
        type = AdsType.SPONSORED,
        clickAction = AdsAction.ShowAppDetails,
        appId = "com.donnnno.arcticons"
    ), Ads(
        title = "Watch YouTube Ad-Free with Clipious",
        subtitle = "An alternative Android client with no ads and more features.",
        imageUrl = "https://raw.githubusercontent.com/lamarios/clipious/master/screenshots/tablet-home_small.png",
        type = AdsType.SPONSORED,
        clickAction = AdsAction.ShowAppDetails,
        appId = "com.github.lamarios.clipious"
    ), Ads(
        title = "FileManagerSphere is coming to Accrescent!!",
        subtitle = "FileManagerSphere, a customizable and modern file manager",
        imageUrl = "https://i.ibb.co/Tt6n2hW/Page-1.png",
        type = AdsType.SPONSORED,
        clickAction = AdsAction.OpenLink("https://github.com/Ruan625Br/FileManagerSphere"),
    )))



    fun refreshRepoData() {
        viewModelScope.launch {
            error = null
            isRefreshing = true

            val context = getApplication<Accrescent>().applicationContext

            try {
                repoDataRepository.fetchRepoData()
            } catch (e: ConnectException) {
                error = context.getString(R.string.network_error, e.message)
            } catch (e: FileNotFoundException) {
                error = context.getString(R.string.failed_download_repodata, e.message)
            } catch (e: GeneralSecurityException) {
                error = context.getString(R.string.failed_verify_repodata, e.message)
            } catch (e: SerializationException) {
                error = context.getString(R.string.failed_decode_repodata, e.message)
            } catch (e: UnknownHostException) {
                error = context.getString(R.string.unknown_host_error, e.message)
            }

            isRefreshing = false
        }
    }

    fun refreshInstallStatuses() {
        viewModelScope.launch {
            apps.collect()
        }
    }

    private fun setupAds(app: App){
       val matchingAd = adsList.find { it.appId == app.id }
       matchingAd?.let {
           adsList = adsList.toMutableList().apply {
               val index = indexOf(it)
               set(index, matchingAd.copy(app = app))
           }
       }
    }
}
