package com.sl.biorhythms

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class BiorhythmsViewModel(
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    private val _birthDate = MutableStateFlow<LocalDate?>(null)
    val birthDate: StateFlow<LocalDate?> = _birthDate.asStateFlow()

    private val _referenceDate = MutableStateFlow(LocalDate.now())
    val referenceDate: StateFlow<LocalDate> = _referenceDate.asStateFlow()

    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.SYSTEM)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.data.collect { prefs ->
                _birthDate.value = prefs[PreferencesKeys.BirthDate]?.let(LocalDate::ofEpochDay)
                _themeMode.value = AppThemeMode.fromStored(prefs[PreferencesKeys.ThemeMode])
                _language.value = AppLanguage.fromStored(prefs[PreferencesKeys.Language])
            }
        }
    }

    fun onBirthDateSelected(date: LocalDate, onPersisted: () -> Unit = {}) {
        _birthDate.value = date
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.BirthDate] = date.toEpochDay()
            }
            onPersisted()
        }
    }

    fun refreshReferenceDate() {
        _referenceDate.value = LocalDate.now()
    }

    fun onThemeModeSelected(mode: AppThemeMode, onPersisted: () -> Unit = {}) {
        _themeMode.value = mode
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.ThemeMode] = mode.storedValue
            }
            onPersisted()
        }
    }

    fun onLanguageSelected(language: AppLanguage, onPersisted: () -> Unit = {}) {
        _language.value = language
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.Language] = language.storedValue
            }
            onPersisted()
        }
    }
}

class BiorhythmsViewModelFactory(
    private val dataStore: DataStore<Preferences>,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BiorhythmsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BiorhythmsViewModel(dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
