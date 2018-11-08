package hu.doboadam.howtube.repository

import android.content.Context

class SharedPrefRepository(context: Context) {
    private val pref = context.getSharedPreferences("FoodTube", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FIRST_RUN = "key.first_run"
    }

    fun setFirstRun(firstRun: Boolean){
        pref.apply {
            edit().putBoolean(KEY_FIRST_RUN, firstRun).apply()
        }
    }

    fun isFirstRun() = pref.getBoolean(KEY_FIRST_RUN, true)
}