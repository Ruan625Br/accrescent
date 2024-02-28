package app.accrescent.client.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.accrescent.client.BuildConfig
import app.accrescent.client.R
import app.accrescent.client.data.InstallStatus
import app.accrescent.client.ui.theme.InterFontFamily
import app.accrescent.client.util.isPrivileged
import app.accrescent.client.util.isSdkVersionCompatible

@Composable
fun InstallActionsButtons(
    minSdkVersion: Int,
    installStatus: InstallStatus,
    enabled: Boolean,
    appId: String,
    onUninstallClicked: () -> Unit,
    onInstallClicked: () -> Unit,
    onOpenAppInfoClicked: () -> Unit,
    onOpenClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isSdkVersionCompatible(minSdkVersion)) {
        ActionsButton(
            modifier = modifier,
            installStatus = installStatus,
            enabled = enabled,
            appId = appId,
            onUninstallClicked = onUninstallClicked,
            onInstallClicked = onInstallClicked,
            onOpenAppInfoClicked = onOpenAppInfoClicked,
            onOpenClicked = onOpenClicked
        )
    } else {
        IncompatibleVersionErrorMessage(
            modifier = modifier
        )
    }
}

@Composable
fun ActionsButton(
    installStatus: InstallStatus,
    enabled: Boolean,
    appId: String,
    onUninstallClicked: () -> Unit,
    onInstallClicked: () -> Unit,
    onOpenAppInfoClicked: () -> Unit,
    onOpenClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isPrivilegedApp = context.isPrivileged() && appId == BuildConfig.APPLICATION_ID

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        when (installStatus) {
            InstallStatus.INSTALLED,
            InstallStatus.UPDATABLE,
            InstallStatus.DISABLED -> {

                if (!isPrivilegedApp) {
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 6.dp),
                        onClick = { onUninstallClicked() },
                    ) {
                        Text(stringResource(R.string.uninstall))
                    }
                }
            }

            else -> Unit
        }
        if (!(installStatus == InstallStatus.INSTALLED && appId == BuildConfig.APPLICATION_ID)) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 6.dp),
                enabled = enabled,
                onClick = {
                    when (installStatus) {
                        InstallStatus.INSTALLABLE,
                        InstallStatus.UPDATABLE -> {
                            onInstallClicked()
                        }

                        InstallStatus.DISABLED -> onOpenAppInfoClicked()
                        InstallStatus.INSTALLED -> onOpenClicked()
                        InstallStatus.LOADING,
                        InstallStatus.UNKNOWN -> Unit
                    }
                },
            ) {
                when (installStatus) {
                    InstallStatus.INSTALLABLE ->
                        Text(stringResource(R.string.install))

                    InstallStatus.UPDATABLE ->
                        Text(stringResource(R.string.update))

                    InstallStatus.DISABLED ->
                        Text(stringResource(R.string.enable))

                    InstallStatus.INSTALLED ->
                        Text(stringResource(R.string.open))

                    InstallStatus.LOADING ->
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 3.dp
                        )

                    InstallStatus.UNKNOWN ->
                        Text(stringResource(R.string.unknown))
                }
            }
        }
    }
}

@Composable
private fun IncompatibleVersionErrorMessage(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.error_incompatible_version_message),
            fontFamily = InterFontFamily,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.error
        )
    }
}