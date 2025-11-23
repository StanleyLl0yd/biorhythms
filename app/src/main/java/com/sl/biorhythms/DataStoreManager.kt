package com.sl.biorhythms

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Единственный экземпляр DataStore для всего приложения
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "biorhythms_prefs")