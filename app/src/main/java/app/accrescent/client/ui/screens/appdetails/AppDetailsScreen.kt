package app.accrescent.client.ui.screens.appdetails

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import app.accrescent.client.R
import app.accrescent.client.data.DownloadProgress
import app.accrescent.client.data.InstallStatus
import app.accrescent.client.data.LinkTextData
import app.accrescent.client.data.MediaListInfo
import app.accrescent.client.data.db.App
import app.accrescent.client.ui.components.ActionConfirmDialog
import app.accrescent.client.ui.components.AppIcon
import app.accrescent.client.ui.components.AppImages
import app.accrescent.client.ui.components.AppVariants
import app.accrescent.client.ui.components.InstallActionsButtons
import app.accrescent.client.ui.components.LinkText
import app.accrescent.client.ui.theme.InterFontFamily
import app.accrescent.client.util.isPrivileged
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun AppDetailsScreen(
    snackbarHostState: SnackbarHostState,
    onNavigateToMediaView: (MediaListInfo) -> Unit,
    onNavigateToAppDetails: (App) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val installStatus = viewModel.installStatuses[viewModel.uiState.appId]
    val downloadProgress = viewModel.downloadProgresses[viewModel.uiState.appId]
    val requireUserAction by viewModel.requireUserAction.collectAsState(!context.isPrivileged())

    var installConfirmDialog by remember { mutableStateOf(false) }
    var uninstallConfirmDialog by remember { mutableStateOf(false) }

    val appLinks = viewModel.appLinks.toImmutableList()
    val appVariants = viewModel.appsVariants.toImmutableList()

    when {
        viewModel.uiState.isFetchingData -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(72.dp))
            }
        }

        viewModel.uiState.appExists -> AppDetails(
            id = viewModel.uiState.appId,
            name = viewModel.uiState.appName,
            versionName = viewModel.uiState.versionName,
            versionCode = viewModel.uiState.versionCode,
            shortDescription = viewModel.uiState.shortDescription,
            installStatus = installStatus ?: InstallStatus.LOADING,
            appLinks = appLinks,
            appVariants = appVariants,
            onInstallClicked = {
                if (context.isPrivileged() && installStatus == InstallStatus.INSTALLABLE && requireUserAction) {
                    installConfirmDialog = true
                } else {
                    viewModel.installApp(viewModel.uiState.appId)
                }
            },
            onUninstallClicked = {
                // When uninstalling in privileged mode, the OS doesn't create a
                // confirmation dialog. To prevent users from mistakenly deleting
                // important app data, create our own dialog in this case.
                if (context.isPrivileged()) {
                    uninstallConfirmDialog = true
                } else {
                    viewModel.uninstallApp(viewModel.uiState.appId)
                }
            },
            onOpenClicked = { viewModel.openApp(viewModel.uiState.appId) },
            onOpenAppInfoClicked = { viewModel.openAppInfo(viewModel.uiState.appId) },
            onNavigateToMediaView = onNavigateToMediaView,
            onNavigateToAppDetails = onNavigateToAppDetails,
            downloadProgress = downloadProgress,
            modifier = modifier,
        )

        else -> AppNotFoundError(modifier)
    }

    if (installConfirmDialog) {
        ActionConfirmDialog(title = stringResource(R.string.install_confirm),
            description = stringResource(R.string.install_confirm_desc),
            onDismiss = { installConfirmDialog = false },
            onConfirm = { viewModel.installApp(viewModel.uiState.appId) })
    }
    if (uninstallConfirmDialog) {
        ActionConfirmDialog(title = stringResource(R.string.uninstall_confirm),
            description = stringResource(R.string.uninstall_confirm_desc),
            onDismiss = { uninstallConfirmDialog = false },
            onConfirm = { viewModel.uninstallApp(viewModel.uiState.appId) })
    }

    if (viewModel.uiState.error != null) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(message = viewModel.uiState.error!!)
            viewModel.uiState.error = null
        }
    }
}

@Composable
fun AppDetails(
    id: String,
    name: String,
    versionName: String,
    versionCode: Long,
    shortDescription: String,
    installStatus: InstallStatus,
    appLinks: ImmutableList<LinkTextData>,
    appVariants: ImmutableList<App>,
    onNavigateToMediaView: (MediaListInfo) -> Unit,
    onNavigateToAppDetails: (App) -> Unit,
    onInstallClicked: () -> Unit,
    onUninstallClicked: () -> Unit,
    onOpenClicked: () -> Unit,
    onOpenAppInfoClicked: () -> Unit,
    downloadProgress: DownloadProgress?,
    modifier: Modifier = Modifier,
) {

    LazyColumn(
        modifier = modifier
    ) {
        item {
            ConstraintLayout(
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                val (iconRef, appNameRef, appAuthorRef, appDownloadProgressRef, installActionsButtonsRef) = createRefs()
                val (aboutThisAppTextRef, aboutTextRef, appImageList, whatsNewTextRef, versionTextRef, versionHistoryTextRef) = createRefs()
                val (whatsNewAppTextRef, appLinksRef, appVariantsRef, appVariantsTextRef) = createRefs()
                val isInstallingAppInProgress = downloadProgress != null
                var waitingForSize by remember { mutableStateOf(false) }
                val iconSize by animateDpAsState(
                    targetValue = if (isInstallingAppInProgress || waitingForSize) 60.dp else 80.dp,
                    label = "icon padding"
                )
                val whatsNewText = """
    - Implemented list for categories
    - Added feature to allow users to customize the corner family
    - Reduced time to launch the video/image viewer
    - Added fast scrolling in file list
    - Various bug fixes and performance improvements for a smoother experience.
""".trimIndent()
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .constrainAs(iconRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }, contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        modifier = Modifier.size(iconSize), appId = id
                    )
                    if (waitingForSize && !isInstallingAppInProgress) {
                        CircularProgressIndicator(modifier = Modifier.size(96.dp))
                    } else if (isInstallingAppInProgress) {
                        waitingForSize = false
                        CircularProgressIndicator(
                            progress = { downloadProgress!!.part.toFloat() / downloadProgress.total },
                            modifier = Modifier.size(96.dp),
                        )
                    }
                }

                Text(
                    modifier = Modifier.constrainAs(appNameRef) {
                        top.linkTo(iconRef.top, margin = 10.dp)
                        start.linkTo(iconRef.end, margin = 10.dp)
                    },
                    text = name,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )

                Text(
                    modifier = Modifier.constrainAs(appAuthorRef) {
                        top.linkTo(appNameRef.bottom)
                        start.linkTo(appNameRef.start)
                    },
                    text = "Author here",
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    color = Color(0xFFA8A9AD)
                )

                if (isInstallingAppInProgress) {
                    val partMb = "%.1f".format(downloadProgress!!.part.toFloat() / 1_000_000)
                    val totalMb = "%.1f".format(downloadProgress.total.toFloat() / 1_000_000)

                    Text(
                        modifier = Modifier.constrainAs(appDownloadProgressRef) {
                            top.linkTo(appAuthorRef.bottom)
                            start.linkTo(appAuthorRef.start)
                        },
                        text = "$partMb MB / $totalMb MB",
                        fontFamily = InterFontFamily,
                        fontSize = 14.sp,
                    )
                }


                InstallActionsButtons(
                    modifier = Modifier.constrainAs(installActionsButtonsRef) {
                        top.linkTo(iconRef.bottom, margin = 20.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    installStatus = installStatus,
                    enabled = !isInstallingAppInProgress && !waitingForSize,
                    appId = id,
                    onUninstallClicked = onUninstallClicked,
                    onInstallClicked = {
                        waitingForSize = true
                        onInstallClicked()
                    },
                    onOpenAppInfoClicked = onOpenAppInfoClicked,
                    onOpenClicked = onOpenClicked
                )

                Text(
                    modifier = Modifier.constrainAs(aboutThisAppTextRef) {
                        top.linkTo(installActionsButtonsRef.bottom, margin = 20.dp)
                        start.linkTo(parent.start)
                    },
                    text = stringResource(id = R.string.about_this_app),
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
                Text(
                    modifier = Modifier.constrainAs(aboutTextRef) {
                        top.linkTo(aboutThisAppTextRef.bottom, margin = 10.dp)
                        start.linkTo(parent.start)
                    }, text = shortDescription, fontFamily = InterFontFamily, fontSize = 16.sp
                )

                AppImages(
                    modifier = Modifier.constrainAs(appImageList) {
                        top.linkTo(aboutTextRef.bottom, margin = 25.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }, appName = name, appId = id, onImageClick = onNavigateToMediaView
                )

                Text(
                    modifier = Modifier.constrainAs(whatsNewTextRef) {
                        top.linkTo(appImageList.bottom, margin = 18.dp)
                        start.linkTo(parent.start)
                    },
                    text = "What’s New",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                Text(
                    modifier = Modifier.constrainAs(versionTextRef) {
                        top.linkTo(whatsNewTextRef.bottom, margin = 5.dp)
                        start.linkTo(parent.start)
                    },
                    text = "Version: $versionName\nCode: $versionCode",
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp
                )

                Text(
                    modifier = Modifier.constrainAs(versionHistoryTextRef) {
                        top.linkTo(appImageList.bottom, margin = 18.dp)
                        end.linkTo(parent.end)
                    },
                    text = "Version History",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    modifier = Modifier.constrainAs(whatsNewAppTextRef) {
                        top.linkTo(versionTextRef.bottom, margin = 15.dp)
                        start.linkTo(parent.start)
                    }, text = whatsNewText, fontFamily = InterFontFamily, fontSize = 14.sp
                )
                Text(
                    modifier = Modifier.constrainAs(appVariantsTextRef) {
                        top.linkTo(whatsNewAppTextRef.bottom, margin = 10.dp)
                        start.linkTo(parent.start)
                    },
                    text = "App Variants",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                AppVariants(
                    modifier = Modifier.constrainAs(appVariantsRef) {
                        top.linkTo(appVariantsTextRef.bottom, margin = 10.dp)
                        start.linkTo(parent.start)
                    },
                    apps = appVariants, onNavigateToAppDetails = onNavigateToAppDetails
                )

                AppLinks(
                    modifier = Modifier.constrainAs(appLinksRef) {
                        top.linkTo(appVariantsRef.bottom)
                        start.linkTo(parent.start)
                    }, links = appLinks
                )
            }

        }
    }

}

@Composable
fun AppNotFoundError(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.error,
            ),
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    stringResource(R.string.cant_find_app),
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun AppLinks(
    links: ImmutableList<LinkTextData>, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {

        HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))

        links.forEach { link ->
            ListItem(leadingContent = {
                link.linkType.icon?.let { icon ->
                    Icon(imageVector = icon, contentDescription = null)
                }
            }, headlineContent = {
                LinkText(linkTextData = link)
            })
        }
    }
}
