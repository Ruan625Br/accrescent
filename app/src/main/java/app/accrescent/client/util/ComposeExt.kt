package app.accrescent.client.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity

enum class KeyboardState {
    Opened,
    Closed
}
@Composable
fun keyboardVisibilityAsState(): State<KeyboardState> {
    return rememberUpdatedState(
        newValue = if (WindowInsets.ime.getBottom(LocalDensity.current) > 0) KeyboardState.Opened
        else KeyboardState.Closed
    )
}