package com.sl.biorhythms

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.IOException
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
            dataStore.data
                .catch { error ->
                    if (error is IOException) {
                        Log.e(TAG, "Unable to read preferences", error)
                        emit(emptyPreferences())
                    } else {
                        throw error
                    }
                }
                .collect { prefs ->
                    _birthDate.value = storedBirthDate(prefs[PreferencesKeys.BirthDate])
                    _themeMode.value = AppThemeMode.fromStored(prefs[PreferencesKeys.ThemeMode])
                    _language.value = AppLanguage.fromStored(prefs[PreferencesKeys.Language])
                }
        }
    }

    fun onBirthDateSelected(date: LocalDate, onPersisted: () -> Unit = {}) {
        val validDate = validatedBirthDate(date)
        val previous = _birthDate.value
        _birthDate.value = validDate
        viewModelScope.launch {
            try {
                dataStore.edit { prefs ->
                    prefs[PreferencesKeys.BirthDate] = validDate.toEpochDay()
                }
            } catch (error: IOException) {
                if (_birthDate.value == validDate) {
                    _birthDate.value = previous
                }
                Log.e(TAG, "Unable to persist birth date", error)
                return@launch
            }
            onPersisted()
        }
    }

    fun refreshReferenceDate() {
        _referenceDate.value = LocalDate.now()
    }

    fun onThemeModeSelected(mode: AppThemeMode, onPersisted: () -> Unit = {}) {
        val previous = _themeMode.value
        _themeMode.value = mode
        viewModelScope.launch {
            try {
                dataStore.edit { prefs ->
                    prefs[PreferencesKeys.ThemeMode] = mode.storedValue
                }
            } catch (error: IOException) {
                if (_themeMode.value == mode) {
                    _themeMode.value = previous
                }
                Log.e(TAG, "Unable to persist theme", error)
                return@launch
            }
            onPersisted()
        }
    }

    fun onLanguageSelected(language: AppLanguage, onPersisted: () -> Unit = {}) {
        val previous = _language.value
        _language.value = language
        viewModelScope.launch {
            try {
                dataStore.edit { prefs ->
                    prefs[PreferencesKeys.Language] = language.storedValue
                }
            } catch (error: IOException) {
                if (_language.value == language) {
                    _language.value = previous
                }
                Log.e(TAG, "Unable to persist language", error)
                return@launch
            }
            onPersisted()
        }
    }

    private companion object {
        const val TAG = "BiorhythmsViewModel"
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
