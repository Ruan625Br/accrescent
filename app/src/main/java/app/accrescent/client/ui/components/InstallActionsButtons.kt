package app.accrescent.client.ui.components

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
import app.accrescent.client.BuildConfig
import app.accrescent.client.R
import app.accrescent.client.data.InstallStatus
import app.accrescent.client.util.isPrivileged

@Composable
fun InstallActionsButtons(
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
        if (installStatus == InstallStatus.DISABLED && !isPrivilegedApp) {
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