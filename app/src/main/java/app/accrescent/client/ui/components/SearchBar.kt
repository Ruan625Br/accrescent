package app.accrescent.client.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import app.accrescent.client.ui.screens.search.SearchSortOrder
import app.accrescent.client.ui.theme.Shapes
import app.accrescent.client.util.KeyboardState
import app.accrescent.client.util.keyboardVisibilityAsState

@Composable
fun SearchBar(
    query: TextFieldValue,
    sortOrder: SearchSortOrder,
    onQueryChange: (TextFieldValue) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    onSortOrderChanged: (SearchSortOrder) -> Unit
) {
    val focusRequester = remember {
        FocusRequester()
    }
    val keyboardState by keyboardVisibilityAsState()
    val focusManager = LocalFocusManager.current
    var isSearchBarFocused by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = keyboardState, block = {
        if (keyboardState == KeyboardState.Closed) {
            focusManager.clearFocus()
        }
    })

    LaunchedEffect(key1 = Unit, block = {
        focusRequester.requestFocus()
    })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
            )
    ) {
        Box(
            modifier = Modifier
                .padding(all = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = Shapes.large
                )
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        isSearchBarFocused = it.isFocused
                    },
                value = query.copy(selection = TextRange(query.text.length)),
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = "Search apps",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                trailingIcon = {
                    if (query.text.isNotBlank()) {
                        AnimatedContent(isSearchBarFocused, label = "") {
                            if (it) {
                                CleatSearchQueryButton(
                                    onClearClick = onClearClick
                                )
                            } else {
                                SearchSortButton(
                                    sortOrder = sortOrder,
                                    onSortOrderChanged = onSortOrderChanged
                                )
                            }
                        }
                    }
                },
                shape = Shapes.large,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                )
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            color = MaterialTheme.colorScheme.surfaceContainer
        )
    }
}

@Composable
private fun SearchSortButton(
    sortOrder: SearchSortOrder,
    onSortOrderChanged: (SearchSortOrder) -> Unit
) {
    Box {
        var isDropdownExpanded by remember { mutableStateOf(false) }

        TextButton(
            modifier = Modifier
                .padding(end = 4.dp)
                .requiredHeight(48.dp),
            onClick = { isDropdownExpanded = true },
            shape = Shapes.medium,
        ) {
            Spacer(Modifier.requiredWidth(4.dp))
            Text(
                text = sortOrder.value,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.requiredWidth(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Sort,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.requiredWidth(4.dp))
        }

        if (isDropdownExpanded) {
            SortDropdownMenu(
                isDropdownExpanded = isDropdownExpanded,
                onDismiss = { isDropdownExpanded = false },
                onSortOrderChanged = {
                    onSortOrderChanged(it)
                    isDropdownExpanded = false
                }
            )
        }
    }
}

@Composable
private fun SortDropdownMenu(
    isDropdownExpanded: Boolean,
    onDismiss: () -> Unit,
    onSortOrderChanged: (SearchSortOrder) -> Unit
) {
    DropdownMenu(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                shape = Shapes.medium
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = Shapes.medium
            ),
        expanded = isDropdownExpanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = {
                Text(text = "Name")
            }, onClick = { onSortOrderChanged(SearchSortOrder.Name) })

        DropdownMenuItem(
            text = {
                Text(text = "Name")
            }, onClick = { onSortOrderChanged(SearchSortOrder.Name) })
    }
}

@Composable
private fun CleatSearchQueryButton(
    onClearClick: () -> Unit
) {
    IconButton(onClick = onClearClick) {
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = null
        )
    }
}