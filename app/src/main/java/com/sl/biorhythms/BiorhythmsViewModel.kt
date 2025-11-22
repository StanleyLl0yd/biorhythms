package com.sl.biorhythms

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate

private val BirthDateKey = longPreferencesKey("birth_date_epoch")
private val ThemeModeKey = intPreferencesKey("theme_mode")
private val LanguageKey = intPreferencesKey("language")

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun fromStored(value: Int?): AppThemeMode =
            values().getOrElse(value ?: 0) { SYSTEM }
    }
}

enum class AppLanguage {
    SYSTEM, RU, EN;

    companion object {
        fun fromStored(value: Int?): AppLanguage =
            values().getOrElse(value ?: 0) { SYSTEM }
    }
}

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
                _birthDate.value = prefs[BirthDateKey]?.let(LocalDate::ofEpochDay)
                _themeMode.value = AppThemeMode.fromStored(prefs[ThemeModeKey])
                _language.value = AppLanguage.fromStored(prefs[LanguageKey])
            }
        }
    }

    fun onBirthDateSelected(date: LocalDate) {
        _birthDate.value = date
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[BirthDateKey] = date.toEpochDay()
            }
        }
    }

    fun onReferenceDateChanged(date: LocalDate) {
        _referenceDate.value = date
    }

    fun onThemeModeSelected(mode: AppThemeMode) {
        _themeMode.value = mode
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[ThemeModeKey] = mode.ordinal
            }
        }
    }

    fun onLanguageSelected(language: AppLanguage) {
        _language.value = language
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[LanguageKey] = language.ordinal
            }
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