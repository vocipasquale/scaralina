package com.pv.scaralina.data.room

import android.content.Context
import android.util.Log
import org.json.JSONArray

object PreloadHelper {

    private const val FILE_NAME = "termini_default.json"

    suspend fun preloadFromJson(context: Context, dao: TermDao) {
        try {
            val json = context.assets.open(FILE_NAME)
                .bufferedReader()
                .use { it.readText() }

            val jsonArray = JSONArray(json)
            val termini = mutableListOf<TermEntity>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val parola = obj.getString("parola")
                val definizione =
                    if (obj.has("definizione")) obj.optString("definizione") else null

                termini.add(
                    TermEntity(
                        parola = parola,
                        definizione = definizione
                    )
                )
            }

            dao.insertAll(termini)

        } catch (e: Exception) {
            Log.w("ROOM_PRELOAD", "Nessun JSON preload o errore lettura", e)
        }
    }
}
