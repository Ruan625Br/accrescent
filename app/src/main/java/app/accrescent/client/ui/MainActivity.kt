package app.accrescent.client.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import app.accrescent.client.R
import app.accrescent.client.data.InstallStatus
import app.accrescent.client.data.MediaListInfo
import app.accrescent.client.data.ROOT_DOMAIN
import app.accrescent.client.ui.components.SearchBar
import app.accrescent.client.ui.screens.Screen
import app.accrescent.client.ui.screens.appdetails.AppDetailsScreen
import app.accrescent.client.ui.screens.applist.AppList
import app.accrescent.client.ui.screens.mediaview.MediaViewScreen
import app.accrescent.client.ui.screens.search.SearchScreen
import app.accrescent.client.ui.screens.search.SearchSortOrder
import app.accrescent.client.ui.screens.settings.SettingsScreen
import app.accrescent.client.ui.screens.settings.SettingsViewModel
import app.accrescent.client.ui.theme.AccrescentTheme
import app.accrescent.client.util.navigate
import app.accrescent.client.util.parcelable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appId = intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME)

        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val dynamicColor by viewModel.dynamicColor.collectAsState(false)

            AccrescentTheme(dynamicColor = dynamicColor) {
                MainContent(appId)
            }
        }

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && this.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(appId: String?) {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val screens = listOf(Screen.AppList, Screen.InstalledApps, Screen.AppUpdates)
    val showBottomBar =
        navController.currentBackStackEntryAsState().value?.destination?.route in screens.map { it.route }
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()
    val bottomAppBarColor =
        MaterialTheme.colorScheme.surfaceColorAtElevation(BottomAppBarDefaults.ContainerElevation)
            .toArgb()
    val activity = LocalContext.current as? Activity
    SideEffect {
        activity?.window?.navigationBarColor = if (showBottomBar) {
            bottomAppBarColor
        } else {
            surfaceColor
        }
    }
    val searchQuery = remember { mutableStateOf(TextFieldValue()) }

    val startDestination =
        if (appId != null) "${Screen.AppDetails.route}/{appId}" else Screen.AppList.route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val settingsScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
    )

    Scaffold(modifier = if (currentDestination?.route == Screen.Settings.route) {
        Modifier.nestedScroll(settingsScrollBehavior.nestedScrollConnection)
    } else {
        Modifier
    }, snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
        // This little hack is used to ensure smooth transition animations when navigating
        // between AppListScreen and AppDetailsScreen. LookaheadLayout may provide a simpler
        // solution once Compose 1.3.0 becomes stable.
        AnimatedVisibility(
            visible = currentDestination?.route == "${Screen.AppDetails.route}/{appId}",
            enter = fadeIn(animationSpec = tween(400)),
            exit = fadeOut(animationSpec = tween(400)),
        ) {
            CenterAlignedTopAppBar(title = {})
        }
        AnimatedVisibility(
            visible = currentDestination?.route == Screen.Settings.route,
            enter = fadeIn(animationSpec = tween(400)),
            exit = fadeOut(animationSpec = tween(400)),
        ) {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                scrollBehavior = settingsScrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.back_button)
                        )
                    }
                },
            )
        }

        AnimatedVisibility(visible = currentDestination?.route == Screen.Search.route) {
            SearchBar(query = searchQuery.value,
                sortOrder = SearchSortOrder.Name,
                onQueryChange = {
                    searchQuery.value = it
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onClearClick = { searchQuery.value = TextFieldValue() },
                onSortOrderChanged = {

                })
        }
        AnimatedVisibility(
            visible = currentDestination?.route != "${Screen.AppDetails.route}/{appId}" && currentDestination?.route != Screen.Settings.route && currentDestination?.route != Screen.Search.route && currentDestination?.route != Screen.MediaView.route,
            enter = fadeIn(animationSpec = tween(400)),
            exit = fadeOut(animationSpec = tween(400)),
        ) {
            CenterAlignedTopAppBar(title = {}, actions = {
                IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                    Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                }
                IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                    Icon(imageVector = Icons.Rounded.Settings, contentDescription = null)
                }
            })
        }
    }, bottomBar = {
        AnimatedVisibility(
            visible = showBottomBar,
            enter = slideInVertically(animationSpec = tween(400)) { it },
            exit = slideOutVertically(animationSpec = tween(400)) { it },
        ) {
            NavigationBar {
                screens.forEach { screen ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(icon = {
                        Icon(
                            if (selected) screen.navIconSelected!! else screen.navIcon!!,
                            contentDescription = stringResource(screen.resourceId)
                        )
                    },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        })
                }
            }
        }
    }) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable(Screen.AppList.route, enterTransition = {
                when (initialState.destination.route) {
                    Screen.InstalledApps.route, Screen.AppUpdates.route -> slideInHorizontally(
                        animationSpec = tween(350)
                    ) { -it }

                    else -> null
                }
            }, exitTransition = {
                when (targetState.destination.route) {
                    "${Screen.AppDetails.route}/{appId}" -> fadeOut(animationSpec = tween(350))

                    Screen.InstalledApps.route, Screen.AppUpdates.route -> slideOutHorizontally(
                        animationSpec = tween(350)
                    ) { -it }

                    Screen.Settings.route -> fadeOut(animationSpec = tween(350))

                    else -> null
                }
            }) {
                AppList(
                    navController = navController,
                    modifier = Modifier.padding(padding),
                    snackbarHostState = snackbarHostState,
                )
            }
            composable(Screen.InstalledApps.route, enterTransition = {
                when (initialState.destination.route) {
                    Screen.AppList.route -> slideInHorizontally(animationSpec = tween(350)) { it }

                    Screen.AppUpdates.route -> slideInHorizontally(animationSpec = tween(350)) { -it }

                    else -> null
                }
            }, exitTransition = {
                when (targetState.destination.route) {
                    "${Screen.AppDetails.route}/{appId}" -> fadeOut(animationSpec = tween(350))

                    Screen.AppList.route -> slideOutHorizontally(animationSpec = tween(350)) { it }

                    Screen.AppUpdates.route -> slideOutHorizontally(animationSpec = tween(350)) { -it }

                    Screen.Settings.route -> fadeOut(animationSpec = tween(350))

                    else -> null
                }
            }) {
                AppList(
                    navController = navController,
                    modifier = Modifier.padding(padding),
                    snackbarHostState = snackbarHostState,
                    filter = {
                        it == InstallStatus.INSTALLED || it == InstallStatus.UPDATABLE || it == InstallStatus.DISABLED
                    },
                    noFilterResultsText = stringResource(R.string.no_apps_installed),
                )
            }
            composable(Screen.AppUpdates.route, enterTransition = {
                when (initialState.destination.route) {
                    Screen.InstalledApps.route, Screen.AppList.route -> slideInHorizontally(
                        animationSpec = tween(350)
                    ) { it }

                    else -> null
                }
            }, exitTransition = {
                when (targetState.destination.route) {
                    "${Screen.AppDetails.route}/{appId}" -> fadeOut(animationSpec = tween(350))

                    Screen.AppList.route, Screen.InstalledApps.route -> slideOutHorizontally(
                        animationSpec = tween(350)
                    ) { it }

                    Screen.Settings.route -> fadeOut(animationSpec = tween(350))

                    else -> null
                }
            }) {
                AppList(
                    navController = navController,
                    modifier = Modifier.padding(padding),
                    snackbarHostState = snackbarHostState,
                    filter = { it == InstallStatus.UPDATABLE },
                    noFilterResultsText = stringResource(R.string.up_to_date),
                )
            }
            composable("${Screen.AppDetails.route}/{appId}",
                arguments = listOf(navArgument("appId") {
                    type = NavType.StringType
                    defaultValue = appId ?: ""
                }),
                deepLinks = listOf(navDeepLink {
                    uriPattern = "https://${ROOT_DOMAIN}/app/{appId}"
                }),
                enterTransition = {
                    when (initialState.destination.route) {
                        Screen.AppList.route, Screen.InstalledApps.route, Screen.AppUpdates.route -> slideInVertically(
                            animationSpec = tween(400)
                        ) { it } + fadeIn(animationSpec = tween(400))

                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Screen.AppList.route, Screen.InstalledApps.route, Screen.AppUpdates.route -> slideOutVertically(
                            animationSpec = tween(600)
                        ) { it } + fadeOut(animationSpec = tween(400))

                        else -> null
                    }
                }) {
                AppDetailsScreen(modifier = Modifier.padding(padding),
                    snackbarHostState = snackbarHostState,
                    onNavigateToMediaView = {
                        val arg = Bundle().apply { putParcelable("mediaListInfo", it) }
                        navController.navigate(Screen.MediaView.route, arg)
                    })
            }
            composable(Screen.Settings.route, enterTransition = {
                when (initialState.destination.route) {
                    Screen.AppList.route, Screen.InstalledApps.route, Screen.AppUpdates.route -> slideInVertically(
                        animationSpec = tween(400)
                    ) { -it } + fadeIn(animationSpec = tween(400))

                    else -> null
                }
            }, exitTransition = {
                when (targetState.destination.route) {
                    Screen.AppList.route, Screen.InstalledApps.route, Screen.AppUpdates.route -> slideOutVertically(
                        animationSpec = tween(600)
                    ) { -it } + fadeOut(animationSpec = tween(400))

                    else -> null
                }
            }) {
                SettingsScreen(Modifier.padding(padding))
            }

            composable(Screen.Search.route) {
                SearchScreen(modifier = Modifier.padding(padding),
                    searchQuery = searchQuery.value.text,
                    onNavigateToAppDetails = { app ->
                        navController.navigate("${Screen.AppDetails.route}/${app.id}")

                    })
            }

            composable(Screen.MediaView.route) {
                val mediaListInfo = it.arguments?.parcelable<MediaListInfo>("mediaListInfo")
                when {
                    mediaListInfo != null -> {
                        MediaViewScreen(mediaListInfo)
                    }

                    else -> navController.popBackStack()
                }

            }
        }
    }
}
