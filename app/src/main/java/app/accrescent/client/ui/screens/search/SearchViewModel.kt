package app.accrescent.client.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.accrescent.client.data.RepoDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    repoDataRepository: RepoDataRepository,
) : ViewModel() {

    val apps = repoDataRepository.getApps()
    private val _searchText = MutableStateFlow("")
    private val searchText = _searchText.asStateFlow()
    private val _searchedAppsList = MutableStateFlow(apps)
    val searchedAppsList = searchText
        .combine(_searchedAppsList.value) { text, apps ->
            when {
                text.isNotEmpty() -> apps.filter { app ->
                    app.name.contains(text.trim(), ignoreCase = true)
                }

                else -> emptyList()
            }


        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
}