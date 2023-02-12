package kr.co.testnavigation.model.datastore

import android.R
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "NAME_KEY")

class RegiStore<T>
    (
    context: Context
            )
 {
     private val dataStore = context.dataStore
     private var ste : String? = null

     companion object {
         val USER_NAME = stringPreferencesKey("NAME")
         val USER_GENDER = stringPreferencesKey("GENDER")
         val USER_ADDRESS = stringPreferencesKey("ADDRESS")
         val USER_PHONE = stringPreferencesKey("PHONE")
         val USER_SAVEWORD = stringPreferencesKey("SAVEWORD")

         // test 용도
         val USER_TEST = stringPreferencesKey("TEST")
     }

     suspend fun writeSet( t : T, s : T){
        when(t) {
            "NAME" -> dataStore.edit { pref -> pref[USER_NAME] = s.toString() }
            "GENDER" -> dataStore.edit { pref -> pref[USER_GENDER] = s.toString() }
            "ADDRESS" -> dataStore.edit { pref -> pref[USER_ADDRESS] = s.toString() }
            "PHONE" -> dataStore.edit { pref -> pref[USER_PHONE] = s.toString() }
            "SAVEWORD" -> dataStore.edit { pref -> pref[USER_SAVEWORD] = s.toString() }

            // test
            "TEST" -> dataStore.edit { pref -> pref[USER_TEST] = s.toString() }

        }

     }
//     suspend fun writeMapSet( t : T, s : java.util.HashMap<String, String>) {
//         when (t) {
//
//             "SAVETEST" -> dataStore.edit {
//                 it[USER_SAVETEST] ?: s
//             }
//         }
//     }
//
//     fun getMapRead(t: String) : Flow<java.util.HashMap<String, String>?> {
//         return dataStore.data
//             .catch { exception ->
//                 if (exception is IOException) {
//                     emit(emptyPreferences())
//                 } else {
//                     throw exception
//                 }
//             }
//             .map { preferences ->
//                 when(t){
//                     "SAVETEST" -> ad = (preferences[USER_SAVETEST] ?: false) as HashMap<String, String>
//                 }
//                 ad
//             }
//     }

     fun getRead( t : T ) : Flow<String?> {
         return dataStore.data
             .catch { exception ->
                 if (exception is IOException) {
                     emit(emptyPreferences())
                 } else {
                     throw exception
                 }
             }
             .map { preferences ->
                 when(t){
                     "NAME" -> ste = (preferences[USER_NAME] ?: false) as String
                     "GENDER" -> ste = (preferences[USER_GENDER] ?: false) as String
                     "ADDRESS" -> ste = (preferences[USER_ADDRESS] ?: false) as String
                     "PHONE" ->  ste = (preferences[USER_PHONE] ?: false) as String
                     "SAVEWORD" ->  ste = (preferences[USER_SAVEWORD] ?: false) as String
                     //test
                     "TEST" -> ste = (preferences[USER_TEST] ?: false) as String
                 }
                 ste!!
             }
     }
}