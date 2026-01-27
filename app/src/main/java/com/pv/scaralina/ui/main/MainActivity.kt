package com.pv.scaralina.ui.main

import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pv.scaralina.R
import com.pv.scaralina.ScaralinaApp
import com.pv.scaralina.ui.sigla.SiglaActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {

    private lateinit var tilParola: TextInputLayout
    private lateinit var etParola: TextInputEditText
    private lateinit var tvContatore: TextView
    private lateinit var tvDefinizione: TextView
    private lateinit var btnStartStop: Button
    private lateinit var btnReset: Button
    private lateinit var seekBarDurata: SeekBar
    private lateinit var tvDurata: TextView
    private lateinit var btnSiglaActivity: Button

    private var timer: CountDownTimer? = null
    private var isRunning = false
    private var remainingMillis: Long = 0
    private var selectedMinutes = 3L

    private val database by lazy { (application as ScaralinaApp).database }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tilParola = findViewById(R.id.tilParola)
        etParola = findViewById(R.id.etParola)
        tvContatore = findViewById(R.id.tvContatore)
        tvDefinizione = findViewById(R.id.tvDefinizione)
        btnStartStop = findViewById(R.id.btnTimer)
        btnReset = findViewById(R.id.btnResetTimer)
        seekBarDurata = findViewById(R.id.sbDurataTimer)
        tvDurata = findViewById(R.id.tvDurata)
        btnSiglaActivity = findViewById(R.id.btnSiglaActivity)

        // 🔍 Cerca parola
        tilParola.setEndIconOnClickListener {
            val parola = etParola.text.toString().trim()

            if (parola.isEmpty()) {
                tvDefinizione.text = "Inserisci una parola"
                return@setEndIconOnClickListener
            }

            lifecycleScope.launch {
                var definizione = cercaParolaInStorage(parola)

                if (definizione.isBlank()) {
                    definizione = cercaParolaOnline(parola)
                }

                if (definizione.isBlank()) {
                    definizione = "Parola non trovata"
                }

                tvDefinizione.text = definizione
            }
        }

        // SeekBar
        seekBarDurata.max = 10
        seekBarDurata.progress = selectedMinutes.toInt()
        tvDurata.text = "$selectedMinutes min"

        seekBarDurata.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedMinutes = if (progress < 1) 1 else progress.toLong()
                tvDurata.text = "$selectedMinutes min"
                if (!isRunning) {
                    remainingMillis = selectedMinutes * 60 * 1000
                    updateTimerText(remainingMillis)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnStartStop.setOnClickListener {
            if (isRunning) pauseTimer() else startTimer()
        }

        btnReset.setOnClickListener { resetTimer() }

        btnSiglaActivity.setOnClickListener {
            startActivity(Intent(this, SiglaActivity::class.java))
        }

        remainingMillis = selectedMinutes * 60 * 1000
        updateTimerText(remainingMillis)
    }

    // =========================
    // TIMER
    // =========================

    private fun startTimer() {
        timer = object : CountDownTimer(remainingMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingMillis = millisUntilFinished
                updateTimerText(remainingMillis)

                if (millisUntilFinished / 1000 <= 10) {
                    ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
                        .startTone(ToneGenerator.TONE_PROP_BEEP)
                }
            }

            override fun onFinish() {
                isRunning = false
                btnStartStop.text = "Start"
                remainingMillis = selectedMinutes * 60 * 1000
                updateTimerText(remainingMillis)

                ToneGenerator(AudioManager.STREAM_ALARM, 100)
                    .startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD)
            }
        }.start()

        isRunning = true
        btnStartStop.text = "Stop"
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
        btnStartStop.text = "Start"
    }

    private fun resetTimer() {
        timer?.cancel()
        isRunning = false
        remainingMillis = selectedMinutes * 60 * 1000
        updateTimerText(remainingMillis)
        btnStartStop.text = "Start"
    }

    private fun updateTimerText(millis: Long) {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        tvContatore.text = String.format("%02d:%02d", minutes, seconds)
    }

    // =========================
    // STORAGE
    // =========================

    private suspend fun cercaParolaInStorage(parola: String): String =
        withContext(Dispatchers.IO) {
            database.termDao().getByParola(parola)
                ?.let { "${it.parola}: ${it.definizione}" }
                ?: ""
        }

    // =========================
    // NETWORK
    // =========================

    interface WiktionaryApi {
        @GET("w/api.php?action=query&format=json&prop=extracts&exintro=&explaintext=")
        suspend fun getDefinizione(@Query("titles") titolo: String): Map<String, Any>
    }


//    private suspend fun cercaParolaOnline(parola: String): String = withContext(Dispatchers.IO) {
//        try {
//            val okHttpClient = okhttp3.OkHttpClient.Builder()
//                .addInterceptor { chain ->
//                    val request = chain.request().newBuilder()
//                        .header("User-Agent", "Mozilla/5.0 (Android) ScaralinaApp")
//                        .build()
//
//                    Log.i("cercaParolaOnline", "Request URL: ${request.url()}")
//
//                    chain.proceed(request)
//                }
//                .build()
//
//            val retrofit = Retrofit.Builder()
//                .baseUrl("https://it.wiktionary.org/")
//                .client(okHttpClient) // <-- usa il client custom
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//
//            val service = retrofit.create(WiktionaryApi::class.java)
//            val response = service.getDefinizione(parola)
//
//            val pages = ((response["query"] as? Map<*, *>)?.get("pages") as? Map<*, *>) ?: emptyMap<Any, Any>()
//            val firstPage = pages.values.firstOrNull() as? Map<*, *>
//            firstPage?.get("extract") as? String ?: ""
//        } catch (e: Exception) {
//            Log.e("ricerca online", "errore", e)
//            e.cause?.toString() ?: e.toString()
//        }
//    }

    private suspend fun cercaParolaOnline(parola: String): String = withContext(Dispatchers.IO) {
        try {
            val okHttpClient = okhttp3.OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Android) ScaralinaApp")
                        .build()

                    Log.i("cercaParolaOnline", "Request URL: ${request.url()}")

                    chain.proceed(request)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://it.wiktionary.org/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(WiktionaryApi::class.java)
            val response = service.getDefinizione(parola)

            // --- estrazione definizione robusta ---
            val query = response["query"] as? Map<*, *>
            val pages = query?.get("pages") as? Map<*, *>
            val firstPage = pages?.values?.firstOrNull() as? Map<*, *>

            if (firstPage == null) {
                "Parola non trovata"
            } else if (firstPage.containsKey("missing")) {
                "Parola non trovata"
            } else {
                firstPage["extract"] as? String ?: ""
            }
        } catch (e: Exception) {
            Log.e("ricerca online", "errore", e)
            e.cause?.toString() ?: e.toString()
        }
    }



}
