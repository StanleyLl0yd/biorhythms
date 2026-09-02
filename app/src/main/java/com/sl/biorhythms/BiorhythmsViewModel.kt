package com.sl.biorhythms

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sl.biorhythms.notification.NotificationPreferences
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

    private val _notificationPreferences = MutableStateFlow(NotificationPreferences())
    val notificationPreferences: StateFlow<NotificationPreferences> = _notificationPreferences.asStateFlow()

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
                    _notificationPreferences.value = NotificationPreferences.fromPreferences(prefs)
                }
        }
    }

    fun onBirthDateSelected(date: LocalDate, onPersisted: () -> Unit = {}) {
        val validDate = validatedBirthDate(date)
        persistSelection(
            state = _birthDate,
            value = validDate,
            errorMessage = "Unable to persist birth date",
            onPersisted = onPersisted,
        ) { prefs ->
            prefs[PreferencesKeys.BirthDate] = validDate.toEpochDay()
        }
    }

    fun refreshReferenceDate() {
        _referenceDate.value = LocalDate.now()
    }

    fun onThemeModeSelected(mode: AppThemeMode, onPersisted: () -> Unit = {}) {
        persistSelection(
            state = _themeMode,
            value = mode,
            errorMessage = "Unable to persist theme",
            onPersisted = onPersisted,
        ) { prefs ->
            prefs[PreferencesKeys.ThemeMode] = mode.storedValue
        }
    }

    fun onLanguageSelected(language: AppLanguage, onPersisted: () -> Unit = {}) {
        persistSelection(
            state = _language,
            value = language,
            errorMessage = "Unable to persist language",
            onPersisted = onPersisted,
        ) { prefs ->
            prefs[PreferencesKeys.Language] = language.storedValue
        }
    }

    fun onNotificationPreferencesSelected(
        preferences: NotificationPreferences,
        onPersisted: () -> Unit = {},
    ) {
        val validPreferences = preferences.copy(
            hour = preferences.hour.coerceIn(0, 23),
            minute = preferences.minute.coerceIn(0, 59),
        )
        persistSelection(
            state = _notificationPreferences,
            value = validPreferences,
            errorMessage = "Unable to persist notification settings",
            onPersisted = onPersisted,
        ) { prefs ->
            prefs[PreferencesKeys.NotificationEnabled] = validPreferences.enabled
            prefs[PreferencesKeys.NotificationHour] = validPreferences.hour
            prefs[PreferencesKeys.NotificationMinute] = validPreferences.minute
            prefs[PreferencesKeys.NotificationDailySummary] = validPreferences.dailySummary
            prefs[PreferencesKeys.NotificationImportantEvents] = validPreferences.importantEvents
            prefs[PreferencesKeys.NotificationPhysical] = validPreferences.physical
            prefs[PreferencesKeys.NotificationEmotional] = validPreferences.emotional
            prefs[PreferencesKeys.NotificationIntellectual] = validPreferences.intellectual
        }
    }

    private fun <T> persistSelection(
        state: MutableStateFlow<T>,
        value: T,
        errorMessage: String,
        onPersisted: () -> Unit,
        persist: suspend (MutablePreferences) -> Unit,
    ) {
        val previous = state.value
        state.value = value
        viewModelScope.launch {
            try {
                dataStore.edit(persist)
            } catch (error: IOException) {
                if (state.value == value) {
                    state.value = previous
                }
                Log.e(TAG, errorMessage, error)
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
