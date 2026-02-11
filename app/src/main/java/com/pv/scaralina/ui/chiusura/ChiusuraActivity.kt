package com.pv.scaralina.ui.chiusura

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pv.scaralina.R
import com.pv.scaralina.data.Partita
import com.pv.scaralina.ui.partita.PartitaActivity
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ChiusuraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chiusura)

        Log.d("chiusura", "chiusura")

        val llVincitore = findViewById<LinearLayout>(R.id.llVincitore)
        val btnClose = findViewById<ImageButton>(R.id.btnClose)
        val btnNuovaPartita = findViewById<ImageButton>(R.id.btnNuovaPartita)

        var count = 1

        for (giocatore in Partita.giocatori.sortedByDescending { it.getpunteggioTotale() }) {

            val ts = if (count == 1) 84f else 36f
            val colorRes = if (count == 1) R.color.purple_500 else R.color.purple_200

            val textView = TextView(this).apply {
                text = "${giocatore.nome}  ${giocatore.getpunteggioTotale()}"
                textSize = ts
                setTextColor(ContextCompat.getColor(context, colorRes))
                gravity = Gravity.CENTER

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                }
            }

            count++
            llVincitore.addView(textView)
        }

        // ================= FUOCHI D'ARTIFICIO =================

        val konfettiView = findViewById<KonfettiView>(R.id.konfettiView)

        repeat(50) { index ->
            konfettiView.postDelayed({

                val party = Party(
                    speed = 20f,
                    maxSpeed = 50f,
                    damping = 0.9f,
                    spread = 360,
                    angle = 0,
                    timeToLive = 3000,
                    shapes = listOf(Shape.Square, Shape.Circle),
                    colors = listOf(
                        Color.YELLOW,
                        Color.RED,
                        Color.GREEN,
                        Color.MAGENTA,
                        Color.CYAN
                    ),
                    emitter = Emitter(
                        duration = 200,
                        TimeUnit.MILLISECONDS
                    ).perSecond(300),
                    position = Position.Relative(
                        Random.nextDouble(),
                        Random.nextDouble(0.2, 0.8)
                    )
                )

                konfettiView.start(party)

            }, (index * 400).toLong())
        }


        // ================= BUTTONS =================

        btnClose.setOnClickListener {
            finishAndRemoveTask()
        }

        btnNuovaPartita.setOnClickListener {
            Partita.reset()
            startActivity(Intent(this, PartitaActivity::class.java))
        }
    }
}
