package pl.rybson.todoapp.repositories

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import pl.rybson.todoapp.utils.Constants
import pl.rybson.todoapp.viewmodels.LayoutManagerType
import pl.rybson.todoapp.viewmodels.SortOrder
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(@ApplicationContext context: Context) {

    companion object {
        private const val TAG = "PreferencesRepository"
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val LAYOUT_MANAGER_TYPE = preferencesKey<String>("layout_manager_type")
    }

    private val dataStore = context.createDataStore(Constants.DATA_STORE_NAME)

    val sortOrderFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences: ", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[SORT_ORDER] ?: SortOrder.BY_LATEST.name
        }

    suspend fun updateSortOrder(sortOrder: SortOrder) = dataStore.edit { preferences ->
        preferences[SORT_ORDER] = sortOrder.name
    }


    val layoutManagerTypeFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences: ", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[LAYOUT_MANAGER_TYPE] ?: LayoutManagerType.LINEAR.name

        }

    suspend fun updateLayoutManagerType() = dataStore.edit { preferences ->
        if (preferences[LAYOUT_MANAGER_TYPE].equals(LayoutManagerType.STAGGERED.name)) {
            preferences[LAYOUT_MANAGER_TYPE] = LayoutManagerType.LINEAR.name
        } else {
            preferences[LAYOUT_MANAGER_TYPE] = LayoutManagerType.STAGGERED.name
        }
    }
}